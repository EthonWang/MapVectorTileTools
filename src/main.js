/*
 * @Author: wyjq
 * @Date: 2021-11-03 22:46:16
 * @Description: 
 */
import Vue from 'vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VueRouter from 'vue-router'
import router from './router'

import App from './App.vue'

Vue.use(ElementUI);
Vue.use(VueRouter)

Vue.prototype.reqUrl="http://"+window.IPConfig.baseIP+":8002";
Vue.prototype.reqBaseIp="http://"+window.IPConfig.baseIP;

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
  router:router
}).$mount('#app')
