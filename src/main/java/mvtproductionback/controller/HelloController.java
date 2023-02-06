package mvtproductionback.controller;

import io.swagger.annotations.ApiOperation;

import mvtproductionback.entity.response.JsonResult;
import mvtproductionback.util.ResponseResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author wyjq
 * @Date 2022/3/15
 */
@RestController
public class HelloController {

    @CrossOrigin(origins = "*")
    @ApiOperation(value = "hello测试")
    @GetMapping("/hello")
    public JsonResult hello() {
        return ResponseResult.success("hello okkk");
    }
}
