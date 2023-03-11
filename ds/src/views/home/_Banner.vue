<template>
  <section class="header">
    <el-carousel height="450px" :interval="2000" arrow="always">
      <el-carousel-item>
        <div class="background-image">
          <img src="@/assets/images/home/banner-1.jpg" alt="信息安全" />
        </div>
      </el-carousel-item>
      <el-carousel-item>
        <div class="background-image">
          <img src="@/assets/images/home/banner-2.png" alt="信息安全" />
        </div>
      </el-carousel-item>
      <el-carousel-item>
        <div class="background-image">
          <img src="@/assets/images/home/banner-3.jpg" alt="信息安全" />
        </div>
      </el-carousel-item>
    </el-carousel>
    <div class="intro">
      <div>
        <h1>智慧城市数据共享交换</h1>
      </div>
      <router-link v-if="login" to="/user">
        <div class="wrap">
          <button class="button" type="primary">进入我的面板</button>
        </div>
      </router-link>
      <router-link v-else to="/signup">
        <div class="wrap">
          <button class="button" type="primary">去注册</button>
        </div>
      </router-link>
    </div>
  </section>
</template>

<script>
import { getters } from "@/store/store";

export default {
  name: "Banner",
  methods: {},
  computed: {
    login: getters.isLogin,
  },
};
</script>

<style scoped>
.header {
  position: relative;
}

.titles {
  margin: 32px 0;
  font-size: 20px;
  display: grid;
  grid-template-columns: auto auto auto;
}

.intro {
  text-align: center;
  z-index: 10;
  left: 50%;
  width: 800px;
  position: absolute;
  top: 50%;
  transform: translateX(-50%) translateY(-50%);
}

a {
  text-decoration: none;
}

.intro h1 {
  font-weight: 300;
  font-size: 3.625rem;
}

.wrap {
  padding-top: 30px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.button {
  min-width: 300px;
  min-height: 60px;
  font-family: "Nunito", sans-serif;
  font-size: 22px;
  text-transform: uppercase;
  letter-spacing: 1.3px;
  font-weight: 700;
  color: #313133;
  background: linear-gradient(60deg, rgba(129, 206, 230, 0.61) 0%, rgb(79, 151, 209) 100%);
  border: none;
  border-radius: 1000px;
  box-shadow: 12px 12px 24px rgba(79, 157, 209, 0.64);
  transition: all 0.3s ease-in-out 0s;
  cursor: pointer;
  outline: none;
  position: relative;
  padding: 10px;
}

button::before {
  content: "";
  border-radius: 1000px;
  min-width: 300px;
  min-height: 60px;
  box-shadow: 0 0 60px rgba(0, 183, 255, 0.64);
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: all 0.3s ease-in-out 0s;
}

.button:hover,
.button:focus {
  color: #313133;
}

button:hover::before,
button:focus::before {
  opacity: 1;
}

button::after {
  content: "";
  width: 30px;
  height: 30px;
  border-radius: 100%;
  border: 6px solid #45ccf5;
  position: absolute;
  z-index: -1;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: ring 1.5s infinite;
}

button:hover::after,
button:focus::after {
  animation: none;
  display: none;
}

@keyframes ring {
  0% {
    width: 30px;
    height: 30px;
    opacity: 1;
  }
  100% {
    width: 300px;
    height: 300px;
    opacity: 0;
  }
}

.background-image {
  width: 100%;
  height: 450px;
  overflow: hidden;
}

.background-image img {
  width: 100%;
  height: 100%;
}

.background-image:after {
  position: absolute;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  display: block;
  background: linear-gradient(220deg, #5a5a5a40, transparent, transparent);
  content: "";
}

/* Animations */
@keyframes bg-scrolling-reverse {
  100% {
    background-position: 0 50px;
  }
}

/* Main styles */
.animated-background {
  /* img size is 50x50 */
  /* Background data (Original source: https://subtlepatterns.com/grid-me/) */
  background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAIAAACRXR/mAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAIGNIUk0AAHolAACAgwAA+f8AAIDpAAB1MAAA6mAAADqYAAAXb5JfxUYAAABnSURBVHja7M5RDYAwDEXRDgmvEocnlrQS2SwUFST9uEfBGWs9c97nbGtDcquqiKhOImLs/UpuzVzWEi1atGjRokWLFi1atGjRokWLFi1atGjRokWLFi1af7Ukz8xWp8z8AAAA//8DAJ4LoEAAlL1nAAAAAElFTkSuQmCC")
    repeat 0 0;
  animation: bg-scrolling-reverse 0.92s infinite; /* IE 10+ */
  animation-timing-function: linear;
}
</style>