<!--
 * @Author: wyjq
 * @Date: 2021-11-03 22:46:16
 * @Description: 
-->
<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: "App",
};
</script>

<style>
html,
body,
#app {
  margin: 0;
  height: 100%;
  width: 100%;
}

::-webkit-scrollbar{
    width: 5px;
    height: 7px;
}
::-webkit-scrollbar-thumb{
    border-radius: 4px;
    height: 50px;
    background: rgba(93, 92, 92,0.2);
}
::-webkit-scrollbar-track{
    box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
    border-radius: 2px;
    background: rgba(253, 250, 250, 0.4);
}
</style>
