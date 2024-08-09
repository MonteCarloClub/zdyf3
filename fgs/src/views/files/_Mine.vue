<!--<template>-->
<!--  <Card title="文件仓库">-->
<!--    <FilesTable :files="files" />-->
<!--  </Card>-->
<!--</template>-->

<!--<script>-->
<!--// @ is an alias to /src-->
<!--import Card from "@/components/Card.vue";-->
<!--import FilesTable from "@/components/FilesTable.vue";-->
<!--import { fileApi } from "@/api/files";-->
<!--import { getters } from "@/store/store";-->
<!--import { FileDownloader } from "@/mixins/Download";-->
<!--import { FilterEmpty } from "@/mixins/FilterEmpty";-->

<!--export default {-->
<!--  name: "Mine",-->
<!--  mixins: [FileDownloader, FilterEmpty],-->
<!--  components: {-->
<!--    Card,-->
<!--    FilesTable,-->
<!--  },-->
<!--  data() {-->
<!--    return {-->
<!--      files: [],-->
<!--      bookmark: "",-->
<!--    };-->
<!--  },-->

<!--  mounted() {-->
<!--    const tag = "";-->
<!--    const userName = getters.userName();-->
<!--    const { bookmark } = this;-->

<!--    do {-->
<!--      fileApi-->
<!--          .files({userName, tag, bookmark})-->
<!--          .then((_) => {-->
<!--            this.files = this.files.concat(_.contents);-->
<!--            this.bookmark = _.bookmark;-->
<!--          })-->
<!--          .catch(console.log);-->
<!--    } while (this.bookmark !== "")-->
<!--  },-->
<!--};-->
<!--</script>-->

<!--<style scoped>-->
<!--.el-tag {-->
<!--  margin-right: 6px;-->
<!--}-->
<!--</style>-->


<!--br:从src/views/AllFiles.vue粘贴过来的-->
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

    <el-pagination
        @current-change="handleCurrentChange"
        :current-page=currentPage
        :page-size=pageSize
        :pager-count="11"
        layout="prev, pager, next"
        :total=allFiles.length>
    </el-pagination>
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
      files: [],    // 当前页面显示的files
      allFiles: [], // 全部的files
      bookmark: "",
      filter: {},
      currentPage:1,
      pageSize:10,
    };
  },

  /**
   * 自动获取所有文件
   */
  async created() {
    let finished = false;
    const tag = "";
    // 空 为所有人
    const userName = "";
    const size = 10;
    do {
      // const {bookmark} = this;
      let bookmark = this.bookmark;

      console.log("bookmark:" + bookmark)

      // do {
      await fileApi
          .files({userName, tag, size, bookmark})
          .then((_) => {
            this.allFiles = this.allFiles.concat(_.contents);
            this.bookmark = _.bookmark;
            if (_.contents.length < size) {
              finished = true;
            }
          })
          .catch(console.log);
    } while (!finished)
    this.files = this.allFiles.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
  },

  methods: {
    handleCurrentChange(val) {
      this.currentPage = val
      console.log(`当前页: ${this.currentPage}`);
      this.files = this.allFiles.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
    },
    async search() {
      this.currentPage = 1
      this.allFiles = []
      this.bookmark = "";
      const { tag, userName } = this.filter;
      const size = 10;

      let finished = false
      do {
        // const {bookmark} = this;
        let bookmark = this.bookmark

        await fileApi
            .files({userName, tag, size, bookmark})
            .then((_) => {
              this.allFiles = this.allFiles.concat(_.contents);
              this.bookmark = _.bookmark;
              if (_.contents.length < size) {
                finished = true;
              }
            })
            .catch(console.log);
      } while (!finished)
      this.files = this.allFiles.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
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