<template>
  <div class="files">
    <Card>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input placeholder="请输入上传者" v-model="filter.userName" clearable> </el-input>
        </el-col>
        <el-col :span="6">
          <el-input placeholder="请输入包含的标签" v-model="filter.tag" clearable> </el-input>
        </el-col>
        <el-col :span="12">
          <el-button style="float: right" type="primary" @click="search">筛选</el-button>
        </el-col>
      </el-row>
    </Card>

    <Card>
      <FilesTable :files="files" />
    </Card>
  </div>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import FilesTable from "@/components/FilesTable.vue";
import { fileApi } from "@/api/files";
import { FileDownloader } from "@/mixins/Download";
import { FilterEmpty } from "@/mixins/FilterEmpty";

export default {
  name: "AllFiles",
  components: {
    Card,
    FilesTable
  },
  mixins: [FileDownloader, FilterEmpty],
  data() {
    return {
      files: [],
      bookmark: "",
      filter: {},
    };
  },

  /**
   * 自动获取所有文件
   */
  mounted() {
    const tag = "";
    // 空 为所有人
    const userName = "";
    const { bookmark } = this;

    fileApi
      .files({ userName, tag, bookmark })
      .then((_) => {
        this.files = _.contents;
        this.bookmark = _.bookmark;
      })
      .catch(console.log);
  },

  methods: {
    search() {
      this.bookmark = "";
      const { tag, userName } = this.filter;
      const { bookmark } = this;

      fileApi
        .files({ userName, tag, bookmark })
        .then((_) => {
          this.files = _.contents;
          this.bookmark = _.bookmark;
        })
        .catch(console.log);
    },
  },
};
</script>

<style scoped>
.el-tag {
  margin-right: 6px;
}

.files > div {
  margin-top: var(--row-distance, 10px);
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