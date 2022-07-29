package com.jim.magicbox.controller;

import com.jim.magicbox.pojo.vo.Result;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName NetWatchController
 * @Description
 * @Author Jim
 * @Date 2022/7/29 下午9:31
 **/
@AllArgsConstructor
@RestController
@RequestMapping("/watch")
public class NetWatchController {


    @GetMapping(value = "/get")
    public Result get(){
        return Result.ok("hello world");
    }

}