package mvtproductionback.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import mvtproductionback.entity.AddPgDTO;
import mvtproductionback.entity.response.JsonResult;
import mvtproductionback.service.PgSourceService;
import mvtproductionback.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @Description
 * @Author wyjq
 * @Date 2023/1/6
 */

@Slf4j
@CrossOrigin
@RestController
public class PgSourceController {

    @Autowired
    PgSourceService pgSourceService;


    @ApiOperation(value = "添加pg源")
    @PostMapping(value = "/addPgSource")
    public JsonResult addPgSource(@RequestBody AddPgDTO addPgDTO) {
        return pgSourceService.addPgSource(addPgDTO);
    }

    @ApiOperation(value = "获取pg源列表")
    @GetMapping(value = "/getPgSourceList")
    public JsonResult getPgSourceList() {

        return pgSourceService.getPgSourceList();
    }

    @ApiOperation(value = "获取pg内的shp信息")
    @GetMapping(value = "/getPgShpInfo/{ip}/{port}/{pgName}")
    public JsonResult getPgShpInfo(@PathVariable("ip") String ip,
                                   @PathVariable("port") String port,
                                   @PathVariable("pgName") String pgName) {
        return pgSourceService.getPgShpsInfo(ip,port,pgName);
    }

}
