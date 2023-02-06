package mvtproductionback.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import mvtproductionback.entity.CreateCacheTaskDTO;
import mvtproductionback.entity.response.JsonResult;
import mvtproductionback.service.MvtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description
 * @Author wyjq
 * @Date 2022/12/30
 */

@Slf4j
@CrossOrigin
@RestController
public class MvtController {

    @Autowired
    MvtService mvtService;

    @ApiOperation(value = "获取瓦片")
    @GetMapping(value = "/getMvtFromMB/{mbName}/{zoom}/{x}/{y}.pbf")
    public void getMbtilesMvt(
            @PathVariable("mbName") String mbName,
            @PathVariable("zoom") int zoom,
            @PathVariable("x") int x,
            @PathVariable("y") int y,
            HttpServletResponse response){

        mvtService.getMbtilesMvt(mbName,zoom,x,y,response);
    }

    @ApiOperation(value = "生产瓦片存储为mbtiles")
    @PostMapping("/createTileCache") 
    public JsonResult createTileCache(@RequestBody CreateCacheTaskDTO createCacheTaskDTO) {

        return mvtService.createTileCache(createCacheTaskDTO);

    }







}
