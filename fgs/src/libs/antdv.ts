import type { App } from "vue";
import Antd from "ant-design-vue";
// https://stackoverflow.com/questions/69039093/how-to-change-antd-theme-in-vite-config
import "ant-design-vue/dist/antd.less"; // 支持主题色

export function setupAntd(app: App<Element>): void {
  app.use(Antd);
}