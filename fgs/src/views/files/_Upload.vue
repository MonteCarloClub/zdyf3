<template>
  <Card title="上传文件">
    <div class="grid-cols">
      <el-form
        label-position="left"
        ref="uploadForm"
        :rules="uploadRules"
        :model="form"
        label-width="100px"
      >
        <el-form-item prop="file" label="* 选择文件">
          <!-- <label for="input-file">Browse...</label> -->
          <input type="file" id="input-file" />
        </el-form-item>
        <el-form-item prop="policy" label="策略表达式">
          <el-input v-model="form.policy" placeholder="(A AND B AND (C OR D))"></el-input>
        </el-form-item>
        <el-form-item prop="tags" label="标签">
          <el-input v-model="form.tags" placeholder="城市 系统 业务 备注（空格隔开）"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="upload">上传到服务器</el-button>
        </el-form-item>
      </el-form>
    </div>
  </Card>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import { Message, Notification } from "element-ui";
import { fileApi } from "@/api/files";
import { getters } from "@/store/store";

export default {
  name: "Upload",
  components: {
    Card,
  },
  data() {
    return {
      form: {
        tags: "",
        policy: "",
      },
      options: ["shanghai", "myc", "edu", "test"],
      uploadRules: {
        policy: [{ required: true, trigger: "blur", message: "请填写上传策略" }],
        tags: [{ required: true, trigger: "blur", message: "请设置标签" }],
      },
    };
  },

  watch: {},

  async mounted() {},

  methods: {
    selecedFile(param) {
      this.file = param.file;
    },
    handleRemove(file) {
      this.$refs.upload.abort(file);
    },
    upload() {
      const file = document.querySelector("input[type=file]").files[0];
      console.log(file);

      if (!file) {
        Notification.error({
          title: "拒绝",
          message: "请添加文件",
          duration: 2000,
        });
        return;
      }

      let tags = this.form.tags.split(" ");

      if (tags.length < 3) {
        Notification.error({
          title: "拒绝",
          message: "请补充完整的标签",
          duration: 2000,
        });
        return;
      }

      this.$refs.uploadForm.validate((valid) => {
        if (!valid) return;

        const userName = getters.userName();
        const policy = this.form.policy;

        fileApi
          .encrypt({ file, userName, tags, policy })
          .then((res) => {
            Message({
              message: "上传成功",
              duration: 5000,
              type: "success",
            });
            console.log(res);
          })
          .catch((e) => {
            Message({
              message: e.message,
              duration: 5000,
              type: "error",
            });
          });
      });
    },
  },
};
</script>
<style scoped>
label {
   cursor: pointer;
   /* Style as you please, it will become the visible UI component. */
}

#input-file {
   /* opacity: 0; */
   /* position: absolute; */
   z-index: 1;
}
</style>