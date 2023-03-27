import { createApp } from 'vue'
import App from './App.vue'

const app = createApp(App);

import router, { setupRouter } from "@/router";
setupRouter(app);

// 设置路由访问的权限
import { setPermissionGuard } from "@/router/permission";
setPermissionGuard(router);

import { setupAntd } from "@/libs/antdv";
setupAntd(app);

import { setupPinia } from "@/libs/pinia";
setupPinia(app)

// the router has resolved all async enter hooks 
// and async components that are associated with the initial route.
router.isReady().then(() => {
    app.mount("#app");
});