import axios from 'axios'
// import Vue from "vue";

const instance= axios.create({
    // baseURL:Vue.prototype.reqUrl,
    baseURL:"http://"+window.IPConfig.baseIP+":8002",
    timeout: 100000,
});


export default {

    requestTest() {
        return instance.get("/hello")
    },

    addPgSource(data){
        return instance.post("addPgSource",data)
    },

    getPgSourceList(){
        return instance.get("getPgSourceList")
    },
    
    getPgShpsInfo(ip,port,pgName){
        return instance.get("/getPgShpInfo/"+ip+"/"+port+"/"+pgName)
    },

    createCache(data){
        return instance.post("/createTileCache",data)
    }

}