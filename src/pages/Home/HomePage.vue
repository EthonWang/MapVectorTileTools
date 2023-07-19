<!--
 * @Author: wyjq
 * @Date: 2021-11-04 14:54:24
 * @Description: 
-->
<template>
<div
    class="homepage">
    <el-container>
        <el-header
            class="flex-start-center"
            height="30px"
            style="background-color: #dcdfe6; ">
            <div
                style="width: 280px;">
<!--                <span>Shp预览与瓦片生产工具</span>-->
            <span>地图矢量瓦片生产与可视化系统 </span>
            </div>
            <div>
                <el-row
                    style="height: 10%;">
                    <el-button
                        type="primary"
                        round
                        size="mini"
                        @click="createCacheDialogShow = true">新建缓存</el-button>
                    <el-dialog
                        class="create-cache-dialog"
                        title="新建缓存任务"
                        :visible.sync="createCacheDialogShow"
                        width="90%">
                        <el-form
                            :inline="true"
                            label-position="right"
                            label-width="60px"
                            :model="createCacheForm"
                            style="width:100%">
                            <el-form-item
                                label="名称">
                                <el-input
                                    v-model="createCacheForm.name"
                                    placeholder="缓存任务名称"></el-input>
                            </el-form-item>
                            <el-form-item
                                label="描述">
                                <el-input
                                    v-model="createCacheForm.description"
                                    placeholder="描述"></el-input>
                            </el-form-item>
                        </el-form>
                        <el-divider></el-divider>

                        <div
                            class="flex-space-center">
                            <div
                                style="width: 40%;">
                                <el-row
                                    class="flex-center-center"
                                    style="height: 20%;">
                                    <el-select
                                        v-model="pgSourceSelect"
                                        placeholder="选择pg源"
                                        style="width: 85%;"
                                        @change="handlePgSelect">
                                        <el-option
                                            v-for="item in pgSourceList"
                                            :key="item"
                                            :label="item"
                                            :value="item">
                                        </el-option>
                                    </el-select>

                                    <el-tooltip
                                        content="添加pg源"
                                        placement="bottom"
                                        effect="light">
                                        <el-button
                                            type="primary"
                                            icon="el-icon-circle-plus-outline"
                                            circle
                                            @click="addPgDialogShowInside = true"></el-button>
                                    </el-tooltip>
                                </el-row>
                                <el-table
                                    ref="shpSelectTable"
                                    :data="pgShpList"
                                    style="width: 100%; height: 40%"
                                    height="34vh"
                                    @selection-change="handleShpSelectChange">
                                    <el-table-column
                                        type="index"
                                        width="30">
                                    </el-table-column>
                                    <el-table-column
                                        prop="shpName"
                                        label="名称">
                                    </el-table-column>
                                    <el-table-column
                                        prop="shpType"
                                        label="类型">
                                    </el-table-column>
                                    <el-table-column
                                        type="selection"
                                        width="80">
                                    </el-table-column>
                                </el-table>
                            </div>

                            <div
                                style="width:60%">
                                <el-table
                                    :data="cacheLayers"
                                    style="width: 100%"
                                    height="40vh">
                                    <el-table-column
                                        prop="shpName"
                                        label="名称"
                                        min-width="50"
                                        show-overflow-tooltip>
                                    </el-table-column>
                                    <el-table-column
                                        label="属性"
                                        min-width="80">
                                        <template slot-scope="scope">
                                            <el-input
                                                size="mini"
                                                v-model="scope.row.field">
                                            </el-input>
                                        </template>
                                    </el-table-column>
                                    <el-table-column
                                        label="min"
                                        min-width="45">
                                        <template slot-scope="scope">
                                            <el-input
                                                size="mini"
                                                v-model="scope.row.minzoom">
                                            </el-input>
                                        </template>
                                    </el-table-column>
                                    <el-table-column
                                        label="max"
                                        min-width="45">
                                        <template slot-scope="scope">
                                            <el-input
                                                size="mini"
                                                v-model="scope.row.maxzoom">
                                            </el-input>
                                        </template>
                                    </el-table-column>
                                    <el-table-column
                                        label="bounds"
                                        min-width="100"
                                        show-overflow-tooltip>
                                        <template slot-scope="scope">
                                            <el-input
                                                size="mini"
                                                v-model="scope.row.bounds">
                                            </el-input>
                                        </template>
                                    </el-table-column>
                                </el-table>
                            </div>
                        </div>

                        <span slot="footer" class="dialog-footer">
                            <el-button
                                @click="createCacheDialogShow = false">取 消</el-button>
                            <el-button
                                type="primary"
                                @click="submitCreateCache">确 定</el-button>
                        </span>
                    </el-dialog>

                    <!-- 里面 -->
                    <el-dialog
                        title="-填写信息添加PG源"
                        :visible.sync="addPgDialogShowInside"
                        width="35%">
                        <el-form
                            label-position="right"
                            label-width="60px"
                            :model="addPgForm">
                            <el-form-item
                                label="ip">
                                <el-input
                                    v-model="addPgForm.ip"
                                    placeholder="必选"></el-input>
                            </el-form-item>
                            <el-form-item
                                label="port">
                                <el-input
                                    v-model="addPgForm.port"
                                    placeholder="必选"></el-input>
                            </el-form-item>
                            <el-form-item
                                label="pg名称">
                                <el-input
                                    v-model="addPgForm.pgName"
                                    placeholder="必选"></el-input>
                            </el-form-item>
                            <el-form-item
                                label="用户名">
                                <el-input
                                    v-model="addPgForm.userName"
                                    placeholder="必选"></el-input>
                            </el-form-item>
                            <el-form-item
                                label="密码">
                                <el-input
                                    v-model="addPgForm.password"
                                    placeholder="必选"></el-input>
                            </el-form-item>
                        </el-form>
                        <span slot="footer" class="dialog-footer">
                            <el-button
                                @click="addPgDialogShowInside = false">取 消</el-button>
                            <el-button
                                type="primary"
                                @click="submitAddPg">确 定</el-button>
                        </span>
                    </el-dialog>

                </el-row>
            </div>
        </el-header>

        <el-container>
            <el-aside
                style="background-color: rgb(238, 241, 246); height: calc(100vh - 30px);">
                <div
                    style="height:55%;">
                    <span>选择数据</span>
                    <el-row
                        class="flex-center-center"
                        style="height: 15%;">
                        <el-select
                            v-model="pgSourceSelect"
                            placeholder="请选择"
                            style="width: 85%;"
                            @change="handlePgSelect">
                            <el-option
                                v-for="item in pgSourceList"
                                :key="item"
                                :label="item"
                                :value="item">
                            </el-option>
                        </el-select>

                        <el-tooltip
                            content="添加pg源"
                            placement="bottom"
                            effect="light">
                            <el-button
                                type="primary"
                                icon="el-icon-circle-plus-outline"
                                circle
                                @click="addPgDialogShow = true"></el-button>
                        </el-tooltip>
                        <el-dialog
                            title="填写信息添加PG源"
                            :visible.sync="addPgDialogShow"
                            width="35%">
                            <el-form
                                label-position="right"
                                label-width="60px"
                                :model="addPgForm">
                                <el-form-item
                                    label="ip">
                                    <el-input
                                        v-model="addPgForm.ip"
                                        placeholder="必选"></el-input>
                                </el-form-item>
                                <el-form-item
                                    label="port">
                                    <el-input
                                        v-model="addPgForm.port"
                                        placeholder="必选"></el-input>
                                </el-form-item>
                                <el-form-item
                                    label="pg名称">
                                    <el-input
                                        v-model="addPgForm.pgName"
                                        placeholder="必选"></el-input>
                                </el-form-item>
                                <el-form-item
                                    label="用户名">
                                    <el-input
                                        v-model="addPgForm.userName"
                                        placeholder="必选"></el-input>
                                </el-form-item>
                                <el-form-item
                                    label="密码">
                                    <el-input
                                        v-model="addPgForm.password"
                                        placeholder="必选"></el-input>
                                </el-form-item>
                            </el-form>
                            <span slot="footer" class="dialog-footer">
                                <el-button
                                    @click="addPgDialogShow = false">取 消</el-button>
                                <el-button
                                    type="primary"
                                    @click="submitAddPg">确 定</el-button>
                            </span>
                        </el-dialog>
                    </el-row>

                    <span>数据列表</span>
                    <el-table
                        :data="pgShpList"
                        style="width: 100%;"
                        height="70%">
                        <el-table-column
                            type="index"
                            width="30">
                        </el-table-column>
                        <el-table-column
                            prop="shpName"
                            label="名称">
                        </el-table-column>
                        <el-table-column
                            prop="shpType"
                            label="类型">

                        </el-table-column>
                        <el-table-column
                            label="操作">
                            <template slot-scope="scope">
                                <el-button
                                    type="success"
                                    plain
                                    size="mini"
                                    @click="handleAddLayerToMap(scope.row)">添加</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>

                <div
                    style="height: 40%;">
                    <span>图层列表</span>
                    <el-table
                        :data="layersTableData"
                        style="width: 100%"
                        height="100%">
                        <el-table-column
                            type="index"
                            width="40">
                        </el-table-column>
                        <el-table-column
                            prop="shpName"
                            label="名称">
                        </el-table-column>
                        <el-table-column
                            label="操作">
                            <template slot-scope="scope">
                                <el-switch
                                    v-model="scope.row.show"
                                    @change="handleLayerShowChange(scope.row)" />
                            </template>
                        </el-table-column>
                    </el-table>
                </div>

            </el-aside>

            <el-main
                style="padding: 0px;">
                <div
                    id="map"
                    style="width: 100%;height: 100%;"></div>
            </el-main>
        </el-container>
    </el-container>
</div>
</template>

<script>
import mapboxgl from "mapbox-gl";
import 'mapbox-gl/dist/mapbox-gl.css';
import requestApi from "../../api/requestApi";

var map = null;

export default {
    name: "HomoPage",

    data() {
        return {
            addPgDialogShow: false,
            addPgDialogShowInside: false,
            pgSourceList: [],
            pgSourceSelect: "",
            pgShpList: [],
            addPgForm: {
                "ip": "localhost",
                "pgName": "MVTDB1",
                "password": "123456",
                "port": "5432",
                "userName": "postgres"
            },

            createCacheDialogShow: false,
            createCacheForm: {
                "ip": "localhost",
                "port": "5432",
                "pgName": "MVTDB1",
                "name": "cache_test",
                "description": "测试",
                "layers": [],
            },
            cacheLayers: [],
            shpSelectList: [],

            layersTableData: []
        }
    },

    mounted() {
        this.getPgList()
        this.createEmptyMap()
    },

    watch: {

        shpSelectList: function (newList) {
            this.cacheLayers = []
            for (let i = 0; i < newList.length; i++) {
                let shp = JSON.parse(JSON.stringify(newList[i]));

                let boundsTemp = []
                shp.bounds.forEach(function (item) {
                    console.log(item.toFixed(2));
                    boundsTemp.push(item.toFixed(2))
                })

                let field = []
                shp.shpAttrs.forEach(function (item) {
                    if (item.column_name != "geom")
                        field.push(item.column_name)
                })

                shp.field = field.toString()
                shp.bounds = boundsTemp.toString()
                shp.minzoom = 0
                shp.maxzoom = 22
                this.cacheLayers.push(shp)
            }

            console.log("cacheLayers", this.cacheLayers);
        }
    },

    methods: {
        test() {
            requestApi.requestTest()
                .then((res) => {
                    console.log("test", res);
                })
                .catch((error) => {
                    console.log(error);
                });

        },

        submitAddPg() {
            requestApi.addPgSource(this.addPgForm)
                .then((res) => {
                    console.log("addpg:", res.data);
                })
                .catch((error) => {
                    console.log(error);
                });
        },
        handlePgSelect(val) {
            let pgConnInfo = val.split(/[:_]/)
            requestApi.getPgShpsInfo(pgConnInfo[0], pgConnInfo[1], pgConnInfo[2])
                .then((res) => {
                    this.pgShpList = res.data.data
                })
                .catch((error) => {
                    console.log(error);
                });
        },
        getPgList() {
            requestApi.getPgSourceList()
                .then((res) => {
                    this.pgSourceList = res.data.data
                })
                .catch((error) => {
                    console.log(error);
                });
        },

        submitCreateCache() {
            let pgInfo = this.pgSourceSelect.split(/[:_]/)
            this.createCacheForm.ip = pgInfo[0]
            this.createCacheForm.port = pgInfo[1]
            this.createCacheForm.pgName = pgInfo[2]
            this.createCacheForm.layers = []

            for (let i = 0; i < this.cacheLayers.length; i++) {
                let cacheLayer = JSON.parse(JSON.stringify(this.cacheLayers[i]));
                cacheLayer.bounds = cacheLayer.bounds.split(',')
                cacheLayer.field = cacheLayer.field.split(',')
                this.createCacheForm.layers.push(cacheLayer)
            }

            requestApi.createCache(this.createCacheForm)
                .then((res) => {
                    this.pgSourceList = res.data.data
                })
                .catch((error) => {
                    console.log(error);
                });

        },

        // toggleShpSelect(rows) {
        //   console.log("row:",rows);
        //     if (rows) {

        //       rows.forEach(row => {
        //         this.$refs.shpSelectTable.toggleRowSelection(row);
        //       });
        //     } else {
        //       this.$refs.shpSelectTable.clearSelection();
        //     }
        //   },
        handleShpSelectChange(val) {
            this.shpSelectList = val;
            console.log(this.shpSelectList);
        },

        createEmptyMap() {
            mapboxgl.accessToken =
                "pk.eyJ1Ijoid3lqcSIsImEiOiJjbDBnZDdwajUxMXRzM2htdWxubDh1MzJrIn0.2e2_rdU2nOUvtwltBIZtZg";
            map = new mapboxgl.Map({
                container: "map",
                style: 'mapbox://styles/mapbox/streets-v11', // style URL
                center: [110, 38], // starting position [lng, lat]
                zoom: 3, // starting zoom
                projection: 'mercator',
                preserveDrawingBuffer: true, //为true，则可以使用map.getCanvas().toDataURL()转为PNG
            });
            map

            // 添加比例尺
            var scale = new mapboxgl.ScaleControl({
                maxWidth: 120,
                unit: "imperial",
            });
            map.addControl(scale);
            scale.setUnit("metric");

            // 添加控件缩放按钮和一个指南针.
            var nav = new mapboxgl.NavigationControl();
            map.addControl(nav, "top-right");

            // 添加全局缩放
            map.addControl(new mapboxgl.FullscreenControl());

            //添加定位控件
            // map.addControl(
            //   new mapboxgl.GeolocateControl({
            //     positionOptions: {
            //       enableHighAccuracy: true,
            //     },
            //     trackUserLocation: true,
            //   })
            // );

            //zoom
            map.on("zoom", () => {
                this.zoom = map.getZoom().toFixed(2);
            });

            //center
            map.on("mousemove", (e) => {
                // map.getCanvas().style.cursor = "pointer";

                this.showCenter =
                    "(" +
                    String(e.lngLat.lng.toFixed(5)) +
                    "，" +
                    String(e.lngLat.lat.toFixed(5)) +
                    ")";
            });


          map.on('load', () => {
            map.setFilter('admin-0-boundary-disputed', [
              'all',
              ['==', ['get', 'disputed'], 'true'],
              ['==', ['get', 'admin_level'], 0],
              ['==', ['get', 'maritime'], 'false'],
              ['match', ['get', 'worldview'], ['all', "CN"], true, false]
            ]);
// The "admin-0-boundary" layer shows all boundaries at
// this level that are not disputed.
            map.setFilter('admin-0-boundary', [
              'all',
              ['==', ['get', 'admin_level'], 0],
              ['==', ['get', 'disputed'], 'false'],
              ['==', ['get', 'maritime'], 'false'],
              ['match', ['get', 'worldview'], ['all', "CN"], true, false]
            ]);
// The "admin-0-boundary-bg" layer helps features in both
// "admin-0-boundary" and "admin-0-boundary-disputed" stand
// out visually.
            map.setFilter('admin-0-boundary-bg', [
              'all',
              ['==', ['get', 'admin_level'], 0],
              ['==', ['get', 'maritime'], 'false'],
              ['match', ['get', 'worldview'], ['all', "CN"], true, false]
            ]);
          });

//           map.setFilter('admin-0-boundary-disputed', [
//             'all',
//             ['==', ['get', 'disputed'], 'true'],
//             ['==', ['get', 'admin_level'], 0],
//             ['==', ['get', 'maritime'], 'false'],
//             ['match', ['get', 'worldview'], ['all', "CN"], true, false]
//           ]);
// // The "admin-0-boundary" layer shows all boundaries at
// // this level that are not disputed.
//           map.setFilter('admin-0-boundary', [
//             'all',
//             ['==', ['get', 'admin_level'], 0],
//             ['==', ['get', 'disputed'], 'false'],
//             ['==', ['get', 'maritime'], 'false'],
//             ['match', ['get', 'worldview'], ['all', "CN"], true, false]
//           ]);
// // The "admin-0-boundary-bg" layer helps features in both
// // "admin-0-boundary" and "admin-0-boundary-disputed" stand
// // out visually.
//           map.setFilter('admin-0-boundary-bg', [
//             'all',
//             ['==', ['get', 'admin_level'], 0],
//             ['==', ['get', 'maritime'], 'false'],
//             ['match', ['get', 'worldview'], ['all', "CN"], true, false]
//           ]);

            //选中某元素
            // map.on('mouseenter',this.layersName,  function (e) {
            // map.getCanvas().style.cursor = 'pointer';
            // console.log("eeeeeeeeee gid:", e);
            //   console.log("eeeeeeeeee gid:", e.features[0].properties.gid);
            //   console.log("属性:", e.features)
            // });

            // Change it back to a pointer when it leaves.
            // map.on("mouseleave", this.layersName, function () {
            //   map.getCanvas().style.cursor = "";
            // });

        },

        handleAddLayerToMap(row) {
            console.log(row)
            row["show"] = true
            this.layersTableData.push(JSON.parse(JSON.stringify(row)))
            let mpType = "";
            if (row.shpType == "POLYGON" || row.shpType == "MULTIPOLYGON") {
                mpType = "fill"
            } else if (row.shpType == "LINESTRING" || row.shpType == "MULTILINESTRING") {
                mpType = "line"
            } else {
                mpType = "circle"
            }
            let pgInfo = this.pgSourceSelect.split(/[:_]/)
            let ip = pgInfo[0]
            let port = pgInfo[1]
            let pgName = pgInfo[2]

            map.addSource(row.shpName, {
                type: "vector",
                tiles: ["http://" + window.IPConfig.baseIP + ":8002/mvtPG/" + ip + "/" + port + "/" + pgName + "/" + row.shpName + "/{z}/{x}/{y}.pbf"]
            });

            if (mpType == "fill") {
                map.addLayer({
                    id: row.shpName,
                    source: row.shpName,
                    "source-layer": row.shpName,
                    type: "fill",
                    paint: {
                        "fill-color": "#" + Math.random().toString(16).substr(2, 6),
                        "fill-opacity": 0.5
                    }
                });
            } else if (mpType == "line") {
                map.addLayer({
                    id: row.shpName,
                    source: row.shpName,
                    "source-layer": row.shpName,
                    type: "line",
                    paint: {
                        "line-color": "#" + Math.random().toString(16).substr(2, 6),
                        "line-opacity": 1,
                        "line-width": 2
                    }
                });
            } else {
                map.addLayer({
                    id: row.shpName,
                    source: row.shpName,
                    "source-layer": row.shpName,
                    type: "circle",
                    paint: {
                        "circle-color": "#" + Math.random().toString(16).substr(2, 6),
                        "circle-opacity": 0.5
                    }
                });
            }

        },

        handleLayerShowChange(row) {
            console.log(row)
            if (row.show) {
                this.handleLayoutChange(row.shpName, "visibility", "visible");
            } else {
                this.handleLayoutChange(row.shpName, "visibility", "none");
            }
        },

        addLayerToMap(newLayer) {
            console.log("add new layer：", newLayer);
            map.addLayer(newLayer);
        },

        handleRemoveSource(sourceName) {
            map.removeSource(sourceName);
        },
        handleRemoveLayer(layerName) {
            map.removeLayer(layerName);
        },

        handlePaintChange(layerName, key, value) {
            console.log("paint:", layerName, key, value);
            map.setPaintProperty(layerName, key, value);
        },
        handleLayoutChange(layerName, key, value) {
            console.log("layout:", layerName, key, value);
            map.setLayoutProperty(layerName, key, value);
        },

        handleLayerShowSwitchChange(val, row) {
            if (val) {
                this.handleLayoutChange(row.id, "visibility", "visible");
            } else {
                this.handleLayoutChange(row.id, "visibility", "none");
            }
        },
    }

};
</script>

<style>
.flex-center-center {
    display: flex;
    justify-content: center;
    align-items: center;
}

.flex-space-center {
    display: flex;
    justify-content: space-between;
    align-items: center;

}

.flex-start-center {
    display: flex;
    justify-content: flex-start;
    align-items: center;
}

.create-cache-dialog .el-dialog {
    height: 85vh;
    overflow: auto;
    margin-top: 5vh !important;
}

/* .shpDialog .el-dialog /deep/{
  margin: 5vh auto !important;
} */

/* .shpDialog .el-dialog__body /deep/ {
  height: 90vh;
  overflow: auto;
} */
</style>
