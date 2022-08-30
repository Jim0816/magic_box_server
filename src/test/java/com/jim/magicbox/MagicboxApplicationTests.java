package com.jim.magicbox;

import com.alibaba.fastjson.JSONObject;
import com.jim.magicbox.utils.MongoDBUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MagicboxApplicationTests {

    @Autowired
    private MongoDBUtil mongoDBUtil;

    @Test
    public void test() throws IOException, InterruptedException {
        String path = "/Users/jim/Files/private/工作/上交/测试/手动包/";
        for (int j = 0 ; j < 1000 ; j++){
            for (int i = 1 ; i <= 8 ; i++ ){
                String pathname = path + i + ".json";
                File file = new File(pathname);
                String jsonString = new String(Files.readAllBytes(Paths.get(file.getPath())));
                mongoDBUtil.insert(JSONObject.parseObject(jsonString), "data");
            }
            Thread.sleep(5000);
        }
        //System.out.println(mongoTemplate);
    }

    @Test
    public void testQuery() {
        mongoDBUtil.andQuery("data");
        //System.out.println(mongoTemplate);
    }

}
