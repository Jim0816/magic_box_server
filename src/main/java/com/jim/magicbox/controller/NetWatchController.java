package com.jim.magicbox.controller;

import com.jim.magicbox.pojo.vo.Result;
import com.jim.magicbox.utils.MongoDBUtil;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.stream.Collectors;

/**
 * @ClassName NetWatchController
 * @Description
 * @Author Jim
 * @Date 2022/7/29 下午9:31
 **/
@RestController
@RequestMapping("/watch")
public class NetWatchController {
    @Autowired
    private MongoDBUtil mongoDBUtil;

    @GetMapping(value = "/list")
    public Result get(){
        List<Map> list = mongoDBUtil.andQuery("data");
        list.stream().map(item -> {
            Object val = item.get("_id");
            String value = val.toString();
            item.remove("_id");
            item.put("id", value);
            return item;
        }).collect(Collectors.toList());
        return Result.ok(list);
    }

}