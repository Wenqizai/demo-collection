package com.wenqi.designpattern.command.demo03;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Wenqi Liang
 * @date 9/3/2023
 */
public class GameApplication {
    private Queue<Command> queue = new LinkedList<>();

    public void mainloop(List<Request> requests) {
        while (true) {
            //省略从epoll或者select中获取数据，并封装成Request的逻辑
            //注意设置超时时间，如果很长时间没有接收到请求，就继续下面的逻辑处理。
            for (Request request : requests) {
                Command command = getCommand(request);
                queue.add(command);
            }

            while (!queue.isEmpty()) {
                Command firstCommand = queue.poll();
                firstCommand.execute();
            }
        }
    }

    private static Command getCommand(Request request) {
        Event event = request.getEvent();
        Command command = null;
        if (event.equals(Event.GOT_DIAMOND)) {
            command = new GotDiamondCommand(/*数据*/);
        } else if (event.equals(Event.GOT_STAR)) {
            command = new GotStartCommand(/*数据*/);
        } else if (event.equals(Event.HIT_OBSTACLE)) {
            command = new Hit0_bstacleCommand(/*数据*/);
        } else if (event.equals(Event.ARCHIVE)) {
            command = new ArchiveCommand(/*数据*/);
        } // 一堆 else if
        return command;
    }
}
