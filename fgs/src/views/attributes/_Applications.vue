<template>
  <div>
    <Card title="属性授权">
      <template v-slot:op>
        <el-button size="small" slot="append" @click="syncApplications"> 同步属性 </el-button>
        <el-button size="small" slot="append" type="primary" @click="dialogFormVisible = true">
          申请属性
        </el-button>
      </template>
      <div v-if="appliedAttrMap">
        <el-table :data="formatAttributes(appliedAttrMap)" style="width: 100%">
          <el-table-column show-overflow-tooltip prop="name" label="属性名"> </el-table-column>
          <el-table-column show-overflow-tooltip prop="value" label="属性公钥"> </el-table-column>
        </el-table>
      </div>
      <el-empty v-else :image-size="100" description="没有正在申请的属性"></el-empty>
    </Card>

    <el-dialog title="申请属性" :visible.sync="dialogFormVisible">
      <el-form ref="applyForm" :rules="applyRules" :model="form" label-width="80px">
        <el-form-item prop="attr" label="属性名">
          <el-input v-model="form.attr"></el-input>
        </el-form-item>

        <el-form-item label="属性类型">
          <el-input
            placeholder="请输入用户或组织名"
            v-model="form.belongs"
            class="input-with-select"
          >
            <el-select v-model="form.role" slot="prepend" placeholder="请选择">
              <el-option label="用户属性" value="to"></el-option>
              <el-option label="组织属性" value="org"></el-option>
            </el-select>
          </el-input>
        </el-form-item>

        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="applyForAttr">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import { attrApi } from "@/api/attributes";
import { getters } from "@/store/store";
import { actions } from "@/store/actions";

export default {
  name: "Applications",
  components: {
    Card,
  },

  computed: {
    ...getters.mapUser(["appliedAttrMap"]),
  },

  mounted() {
    // console.log(getters.properties(["appliedAttrMap"]));
  },

  data() {
    return {
      attributes: [],
      statusTypes: {
        已通过: "success",
        被拒绝: "danger",
        未处理: "info",
      },
      dialogFormVisible: false,
      form: {
        attr: "",
        to: "",
        org: "",
        belongs: "",
        role: "to",
        remark: "",
      },
      applyRules: {
        attr: [{ required: true, trigger: "blur", message: "属性名不能为空" }],
      },
    };
  },

  methods: {
    formatAttributes(attrMap) {
      let attributes = [];
      for (let key of Object.keys(attrMap)) {
        attributes.push({
          name: key,
          value: attrMap[key],
        });
      }
      return attributes;
    },

    applyForAttr() {
      const { name } = getters.properties(["name"]);
      if (!name) return;

      this.$refs.applyForm.validate((valid) => {
        if (!valid) return;
        let emm = {
          to: "",
          org: "",
        };

        const { remark, role, belongs } = this.form;
        emm[role] = belongs;
        const attr = `${belongs}:${this.form.attr}`;

        attrApi
          .apply({
            attr,
            name,
            remark,
            ...emm,
          })
          .then(() => {
            this.$message({
              message: "申请成功",
              duration: 2 * 1000,
              type: "success",
            });
          })
          .catch((e) => {
            this.$message({
              message: e.message,
              type: "error",
            });
          })
          .finally(() => {
            this.dialogFormVisible = false;
          });
      });
    },

    syncApplications() {
      actions
        .sync()
        .then(() => {
          this.$message({
            message: "同步成功",
            duration: 2 * 1000,
            type: "success",
          });
        })
        .catch((e) => {
          this.$message({
            message: e.message,
            type: "error",
          });
        });
    },
  },
};
</script>

<style>
.el-select .el-input {
  width: 120px;
}
.input-with-select .el-input-group__prepend {
  background-color: #fff;
}
</style>