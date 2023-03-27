import type { App } from 'vue'
import { BASE_ROUTE } from "@/common/constants";
import { createWebHistory, createRouter } from "vue-router";

const router = createRouter({
    history : createWebHistory(BASE_ROUTE),
    routes: [
        {
            path: '/',
            component: () => import("@/pages/Home.vue")
        },
        {
            path: '/sign',
            component: () => import("@/pages/Sign.vue")
        }
    ]
})

export function setupRouter(app: App<Element>) {
    app.use(router);
}

export default router