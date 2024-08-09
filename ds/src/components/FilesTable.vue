<template>
  <div>
  <el-table :data="showFiles">
    <el-table-column show-overflow-tooltip label="数据源标识" prop="cipher">
      <template slot-scope="scope">
        {{ `${scope.row.cipher}` }}
      </template>
    </el-table-column>
    <el-table-column show-overflow-tooltip label="文件名" prop="fileName" />
    <!-- <el-table-column show-overflow-tooltip label="密文哈希" prop="cipher" /> -->
    <el-table-column show-overflow-tooltip label="上传者" prop="sharedUser" />
    <el-table-column show-overflow-tooltip label="上传时间"  sortable prop="timeStamp" width="250">
      <template slot-scope="scope">
        {{ scope.row.timeStamp }}
      </template>
    </el-table-column>
<!--    <el-table-column show-overflow-tooltip label="IP" prop="ip" width="130"></el-table-column>-->
    <el-table-column show-overflow-tooltip label="加密策略" prop="policy" />
    <el-table-column show-overflow-tooltip label="标签" prop="tags">
      <template slot-scope="scope">
        <el-tag
          v-for="(tag, i) in filterEmpty(scope.row.tags)"
          :key="i"
          size="small"
          effect="plain"
        >
          {{ tag }}
        </el-tag>
      </template>
    </el-table-column>

    <el-table-column label="操作" align="right" width="200">
      <template slot-scope="scope">
        <el-button size="mini" type="success" @click="decryDownload(scope.row)">
          解密下载
        </el-button>
        <el-button size="mini" @click="cipherDownload(scope.row)"> 下载密文 </el-button>
      </template>
    </el-table-column>
  </el-table>

  <button @click="loadData(currentPage + 1)" :disabled="!hasMoreData">Load More</button>

  <el-pagination
      @current-change="handleCurrentChange"
      :current-page=currentPage
  :page-size=pageSize
  :pager-count="11"
  layout="prev, pager, next"
  :total=files.length>
</el-pagination>
</div>
</template>

<script>
// @ is an alias to /src
import { fileApi } from "@/api/files";
import { getters } from "@/store/store";
import { FileDownloader } from "@/mixins/Download";
import { FilterEmpty } from "@/mixins/FilterEmpty";
import { TimeFormat } from "@/mixins/TimeFormat";


export default {
  name: "FilesTable",
  mixins: [FileDownloader, FilterEmpty, TimeFormat],

  props: {
    files: {
      type: Array,
      default: undefined,
    },
  },
  data() {
    return{
      currentPage:1,
      pageSize:10,
    }
    
  },
  computed:{
    showFiles:function(){
      console.log(this.currentPage.value);
      return this.files.slice((this.currentPage-1)*this.pageSize,this.currentPage*this.pageSize)
    },
    // paginatedItems() {
    //   const start = (this.currentPage - 1) * this.pageSize;
    //   const end = start + this.pageSize;
    //   return this.items.slice(start, end);
    // },
    hasMoreData() {
      return this.bookmark !== "";
    }
  },
  methods: {
      handleCurrentChange(val) {
        this.currentPage = val
        console.log(`当前页: ${this.currentPage}`);
      },
    async loadData(page) {
      if (page > this.currentPage) {
        console.log("loadData开始等待loadMoreData了")
        await this.$nextTick();
        this.currentPage = page;
        await this.loadMoreData();
      }
    },
    async loadMoreData() {
        const userName = ""
      const tag = ""
      const size = 10
      const bookmark = this.bookmark
      fileApi
          .files({userName, tag, size, bookmark})
          .then((_) => {
            console.log("loadMoreData返回的结果："+ _.data)
            this.files = this.files.concat(_.data.contents)
            this.bookmark = _.data.bookmark
          })
      this.handleCurrentChange(this.currentPage)
      // try {
      //   let response = await this.$http.post('/api/query', {
      //     queryString: "YOUR_QUERY_STRING",
      //     pageSize: this.pageSize,
      //     bookmark: this.bookmark
      //   });
      //   let data = response.data;
      //   this.items = this.items.concat(data.result);
      //   this.bookmark = data.responseMetadata.bookmark;
      // } catch (error) {
      //   console.error("Error loading data:", error);
      // }
    },
    decryDownload(scope) {
      const user = getters.userName();
      const { cipher, sharedUser, fileName, tags } = scope;
      fileApi
        .decrypt({ user, cipher, sharedUser, fileName, tags })
        .then(() => {
          this.$message({
            message: '解密成功，开始下载',
            duration: 1000,
            type: "info",
          });
          return fileApi
            .download({ fileName, sharedUser })
            .then((_) => {
              this.saveFile(fileName, _.data);
            })
            .catch((message) => {
              this.$message({
                message,
                duration: 5000,
                type: "error",
              });
            });
        })
        .catch((e) => {
          this.$message({
            message: '解密失败',
            duration: 5000,
            type: "error",
          });
          console.log(e);
        });
    },

    cipherDownload(scope) {
      const userName = getters.userName();
      const { sharedUser, fileName } = scope;

      this.$message({
        message: '开始下载',
        duration: 1000,
        type: "info",
      });
      
      fileApi
        .downloadCipher({ userName, fileName, sharedUser })
        .then((_) => {
          this.saveFile("cipher_hash_" + fileName + '.txt', _.data);
        })
        .catch((e) => {
          this.$message({
            message: e,
            duration: 5000,
            type: "error",
          });
        });
    },
  },
 
};
</script>

<style scoped>
.el-tag {
  margin-right: 6px;
}
.el-table{
  
}
</style>