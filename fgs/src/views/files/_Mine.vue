<template>
  <Card title="我的文件">
    <FilesTable :files="files" />
  </Card>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import FilesTable from "@/components/FilesTable.vue";
import { fileApi } from "@/api/files";
import { getters } from "@/store/store";
import { FileDownloader } from "@/mixins/Download";
import { FilterEmpty } from "@/mixins/FilterEmpty";

export default {
  name: "Mine",
  mixins: [FileDownloader, FilterEmpty],
  components: {
    Card,
    FilesTable,
  },
  data() {
    return {
      files: [],
      bookmark: "",
    };
  },

  mounted() {
    const tag = "";
    const userName = getters.userName();
    const { bookmark } = this;

    fileApi
      .files({ userName, tag, bookmark })
      .then((_) => {
        this.files = _.contents;
        this.bookmark = _.bookmark;
      })
      .catch(console.log);
  },
};
</script>

<style scoped>
.el-tag {
  margin-right: 6px;
}
</style>