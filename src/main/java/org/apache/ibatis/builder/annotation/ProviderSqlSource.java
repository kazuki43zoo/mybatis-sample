/**
 * Copyright ${license.git.copyrightYears} the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.builder.annotation;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Clinton Begin
 */
public class ProviderSqlSource implements SqlSource {

    private SqlSourceBuilder sqlSourceParser;
    private Class<?> providerType;
    private Method providerMethod;
    private boolean providerTakesParameterObject;
    private String[] paramNames;

    public ProviderSqlSource(Configuration config, Object provider) {
        String providerMethodName = null;
        try {
            this.sqlSourceParser = new SqlSourceBuilder(config);
            this.providerType = (Class<?>) provider.getClass().getMethod("type").invoke(provider);
            providerMethodName = (String) provider.getClass().getMethod("method").invoke(provider);

            for (Method m : this.providerType.getMethods()) {
                if (providerMethodName.equals(m.getName())) {
                    if (m.getReturnType() == String.class) {
                        if (providerMethod != null) {
                            throw new BuilderException();
                        }
                        this.providerMethod = m;
                        this.providerTakesParameterObject = m.getParameterTypes().length >= 1;
                    }
                }
            }
            if (this.providerMethod == null) {
                throw new BuilderException("Error creating SqlSource for SqlProvider. Method '"
                        + providerMethodName + "' not found in SqlProvider '" + this.providerType.getName() + "'.");
            }
            String[] paramNames = new String[providerMethod.getParameterTypes().length];
            for (int i = 0; i < paramNames.length; i++) {
                Param param = findParamAnnotation(i);
                paramNames[i] = param != null ? param.value() : "param" + (i + 1);
            }
            this.paramNames = paramNames;
        } catch (BuilderException e) {
            throw e;
        } catch (Exception e) {
            throw new BuilderException("Error creating SqlSource for SqlProvider.  Cause: " + e, e);
        }
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        SqlSource sqlSource = createSqlSource(parameterObject);
        return sqlSource.getBoundSql(parameterObject);
    }

    private SqlSource createSqlSource(Object parameterObject) {
        try {
            String sql;
            if (providerTakesParameterObject) {
                if (providerMethod.getParameterTypes().length == 1) {
                    sql = (String) providerMethod.invoke(providerType.newInstance(), parameterObject);
                } else if (parameterObject instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> params = (Map<String, Object>) parameterObject;
                    sql = (String) providerMethod.invoke(providerType.newInstance(), toProviderMethodArgs(params));
                } else {
                    throw new BuilderException();
                }
            } else {
                sql = (String) providerMethod.invoke(providerType.newInstance());
            }
            Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
            return sqlSourceParser.parse(sql, parameterType, new HashMap<String, Object>());
        } catch (BuilderException e) {
            throw e;
        } catch (Exception e) {
            throw new BuilderException("Error invoking SqlProvider method ("
                    + providerType.getName() + "." + providerMethod.getName()
                    + ").  Cause: " + e, e);
        }
    }

    private Object[] toProviderMethodArgs(Map<String, Object> params) {
        Object[] args = new Object[paramNames.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = params.get(paramNames[i]);
        }
        return args;
    }

    private Param findParamAnnotation(int parameterIndex) {
        final Object[] paramAnnotations = providerMethod.getParameterAnnotations()[parameterIndex];
        Param annotation = null;
        for (Object paramAnnotation : paramAnnotations) {
            if (paramAnnotation instanceof Param) {
                annotation = Param.class.cast(paramAnnotation);
                break;
            }
        }
        return annotation;
    }

}
