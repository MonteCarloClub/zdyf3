<template>
  <div class="main-content">
    <div class="">
      <transition-group
        class="grid-rows nav-col"
        tag="div"
        v-on:before-enter="beforeEnter"
        v-on:enter="animEnter"
        appear
      >
        <Info key="-1" data-index="0" />
        <Nav key="2" data-index="1" />
        <Orgs key="3" data-index="1.5" />
      </transition-group>

      <!--        [br]：临时，用于测试DBRecord两个接口-->
<!--      <span>br：加一个临时部分，用于测试DBRecord的两个接口</span>-->
<!--      <el-form>-->
<!--        &lt;!&ndash;          <el-form-item prop="name" label="用户名">&ndash;&gt;-->
<!--        &lt;!&ndash;            <el-input v-model="login.name" placeholder="请输入用户名" maxlength="11"></el-input>&ndash;&gt;-->
<!--        &lt;!&ndash;          </el-form-item>&ndash;&gt;-->

<!--        &lt;!&ndash;          <el-form-item prop="password" label="密码" v-if="useCert === false">&ndash;&gt;-->
<!--        &lt;!&ndash;            <el-input&ndash;&gt;-->
<!--        &lt;!&ndash;                v-model="login.password"&ndash;&gt;-->
<!--        &lt;!&ndash;                placeholder="请输入密码"&ndash;&gt;-->
<!--        &lt;!&ndash;                show-password&ndash;&gt;-->
<!--        &lt;!&ndash;                @keyup.enter.native="onLoginSubmit"&ndash;&gt;-->
<!--        &lt;!&ndash;            ></el-input>&ndash;&gt;-->
<!--        &lt;!&ndash;          </el-form-item>&ndash;&gt;-->

<!--        <el-form-item>-->
<!--          <el-button style="width: 100%" type="primary" @click="onAddDBRecordTest" :loading="loading">-->
<!--            测试add【24.7.15，应该是u1成功上传，后端有本地文件记录】-->
<!--          </el-button>-->
<!--          <el-button style="width: 100%" type="primary" @click="onQueryDBRecordTest" :loading="loading">-->
<!--            测试query【24.7.15，应该u2能够解密field1和3】-->
<!--          </el-button>-->
<!--        </el-form-item>-->
<!--      </el-form>-->

    </div>
    <keep-alive>
      <router-view></router-view>
    </keep-alive>
  </div>
</template>

<script>
import Nav from "./_Nav.vue";
import Info from "./_Info.vue";
import Orgs from "./_Orgs.vue";

export default {
  name: "User",
  components: {
    Nav,
    Info,
    Orgs,
  },
  data() {
    return {
      // [br]：测试DBRecord两个接口
      // addDBRecord: {
      //   caseID: "TestCase1",
      //   policies: {
      //         TestField1 : "(u1:u1a1 AND u1:u1a2)",
      //         TestField2 : "((u1:u1a1 OR u1:u1a2) AND u1:u1a3)",
      //         TestField3 : "(u1:u1a1 OR u1:u1a3)"
      //       },
      //   userID: "u1",
      // },
      // queryDBRecord: {
      //   userID: "u2",
      //   caseID: "TestCase1"
      // }
    };
  },
  methods: {
    beforeEnter: function (el) {
      if (el.dataset.index > -1) {
        el.style.opacity = 0;
        el.style.transform = "translateY(30px)";
      }
    },
    animEnter: function (el, done) {
      var delay = el.dataset.index * 250;
      setTimeout(function () {
        el.style = ""; // 清空初始的偏移样式
        done();
      }, delay);
    },


    // // [br]添加add和query的测试
    // onAddDBRecordTest() {
    //   const payload = {
    //     caseID: this.addDBRecord.caseID,
    //     policies: this.addDBRecord.policies,
    //     userID: this.addDBRecord.userID
    //   }
    //   // const response = await axios.post('http://10.176.40.69/dbRecord/add', payload)
    //
    //   // let response;
    //   const response = request({
    //     url: '/dbRecord/add',
    //     method: 'post',
    //     // payload,
    //     data: payload
    //   })
    //   console.log('Response: ', response);
    // },
    // onQueryDBRecordTest() {
    //   const payload = {
    //     userID: this.queryDBRecord.userID,
    //     caseID: this.queryDBRecord.caseID
    //   }
    //   // const response = await axios.post('http://10.176.40.69/dbRecord/query', payload)
    //
    //   const response = request({
    //     url: '/dbRecord/query',
    //     method: 'post',
    //     // payload,
    //     data: payload
    //   })
    //   console.log('Response: ', response);
    // }
  },
};
</script>

<style scoped>
.grid-rows {
  display: grid;
  grid-template-columns: auto;
  grid-gap: var(--row-distance, 10px);
}

.grid-rows > div {
  transition: all 0.5s;
}

.nav-col {
  width: 300px;
  float: left;
  margin-right: var(--row-distance, 10px);
}
</style>


<style>
/* shared by child components */
.el-table .cell {
  white-space: nowrap !important;
}

.el-descriptions-item__content {
  white-space: nowrap !important;
  overflow: hidden;
  text-overflow: ellipsis;
}

.el-descriptions-item__label {
  white-space: nowrap !important;
}
</style>