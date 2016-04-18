package com.example.domain.model;


import org.springframework.core.annotation.Order;

public class OrderBy {

    private String column;
    private Direction direction = Direction.ASC;
    public OrderBy(String column, Direction direction){
        this.column = column;
        this.direction = direction;
    }
    public enum Direction {
        ASC, DESC;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
