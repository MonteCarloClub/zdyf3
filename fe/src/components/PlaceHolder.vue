<template>
  <div class="placeholder">
    <div class="card">{{ msg }}</div>
  </div>
</template>

<script>
export default {
  name: 'PlaceHolder',
  props: {
    msg: String
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.placeholder {
  text-align: center;
  line-height: 60px;
  padding: var(--row-distance, 10px) 0;
}
</style>
