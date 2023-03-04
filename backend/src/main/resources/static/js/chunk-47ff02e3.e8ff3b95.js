(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-47ff02e3"],{"0970":function(e,t,a){},"438b0":function(e,t,a){},"604e":function(e,t,a){"use strict";a.d(t,"a",(function(){return s}));a("0721");var r=a("b775"),n=a("4f14"),o=a.n(n),s={encrypt:function(e){var t=e.userName,a=e.tags,n=e.file,o=e.policy,s=new FormData;return s.append("fileName",t),s.append("file",n),s.append("tags",a),s.append("policy",o),new Promise((function(e,t){Object(r["a"])({url:"/content/upload",method:"post",headers:{"Content-Type":"multipart/form-data"},data:s,timeout:0}).then((function(a){200===a.code?e(a.data):t(a)})).catch(t)}))},files:function(e){var t=e.userName,a=e.tag,n=e.size,o=e.bookmark,s={userName:t,tag:a,size:n,bookmark:o},i={fromUserName:s.userName,tag:s.tag,pageSize:s.size||10,bookmark:s.bookmark};return new Promise((function(e,t){Object(r["a"])({url:"/content/list",method:"get",data:i,params:i}).then((function(a){200===a.code?e(a.data):t(a)})).catch(t)}))},decrypt:function(e){var t=e.user,a=e.cipher,n=e.sharedUser,o=e.fileName,s=e.tags,i={userName:t,fileName:o,cipher:a,tags:s,sharedUser:n};return new Promise((function(e,t){Object(r["a"])({url:"/content/decryption",method:"post",data:i}).then((function(a){200===a.code?e(a.data):t(a)})).catch(t)}))},download:function(e){var t=e.fileName,a=e.sharedUser,r={fileName:t,sharedUser:a};return new Promise((function(e,t){o.a.request({baseURL:"/abe/",url:"/content/download",method:"get",data:r,params:r,responseType:"blob"}).then((function(a){200===a.status?e(a.data):t(a)})).catch(t)}))},downloadCipher:function(e){var t=e.userName,a=e.fileName,r=e.sharedUser,n={userName:t,fileName:a,sharedUser:r};return new Promise((function(e,t){o.a.request({baseURL:"/abe/",url:"/content/cipher",method:"get",data:n,params:n,responseType:"blob"}).then((function(a){200===a.status?e(a.data):t(a)})).catch(t)}))}}},6957:function(e,t,a){"use strict";a("438b0")},"6b79":function(e,t,a){"use strict";a.d(t,"a",(function(){return r}));a("106c");var r={methods:{formatTime:function(e){var t="-";try{t=new Date(1e3*e);var a=t.getFullYear(),r=t.getMonth()+1,n=t.getDate(),o=t.getHours(),s=t.getMinutes(),i=t.getSeconds(),l="".concat(a,"-").concat(r,"-").concat(n," ").concat(o,":").concat(s,":").concat(i);return l}catch(c){console.log(c)}return t}}}},"752a":function(e,t,a){"use strict";var r=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("el-table",{attrs:{data:e.files}},[a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"文件名",prop:"fileName"}}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"密文哈希",prop:"cipher"}}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"上传者",prop:"sharedUser"}}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"上传时间",prop:"timeStamp",width:"250"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v(" "+e._s(t.row.timeStamp)+" ")]}}])}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"IP",prop:"ip",width:"130"}}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"加密策略",prop:"policy"}}),a("el-table-column",{attrs:{"show-overflow-tooltip":"",label:"标签",prop:"tags"},scopedSlots:e._u([{key:"default",fn:function(t){return e._l(e.filterEmpty(t.row.tags),(function(t,r){return a("el-tag",{key:r,attrs:{size:"small",effect:"plain"}},[e._v(" "+e._s(t)+" ")])}))}}])}),a("el-table-column",{attrs:{label:"操作",align:"right",width:"200"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{attrs:{size:"mini",type:"success"},on:{click:function(a){return e.decryDownload(t.row)}}},[e._v(" 解密下载 ")]),a("el-button",{attrs:{size:"mini"},on:{click:function(a){return e.cipherDownload(t.row)}}},[e._v(" 下载密文 ")])]}}])})],1)},n=[],o=a("604e"),s=a("07a4"),i=a("eeef"),l=a("f778"),c=a("6b79"),u={name:"FilesTable",mixins:[i["a"],l["a"],c["a"]],props:{files:{type:Array,default:void 0}},methods:{decryDownload:function(e){var t=this,a=s["a"].userName(),r=e.cipher,n=e.sharedUser,i=e.fileName,l=e.tags;o["a"].decrypt({user:a,cipher:r,sharedUser:n,fileName:i,tags:l}).then((function(){return o["a"].download({fileName:i,sharedUser:n}).then((function(e){t.saveFile(i,e)})).catch((function(e){t.$message({message:e.message,duration:5e3,type:"error"})}))})).catch((function(e){t.$message({message:e.message,duration:5e3,type:"error"})}))},cipherDownload:function(e){var t=this,a=s["a"].userName(),r=e.sharedUser,n=e.fileName;o["a"].downloadCipher({userName:a,fileName:n,sharedUser:r}).then((function(e){t.saveFile("cipher_hash_"+n,e)})).catch((function(e){t.$message({message:e.message,duration:5e3,type:"error"})}))}}},f=u,d=(a("6957"),a("52e0")),m=Object(d["a"])(f,r,n,!1,null,"3b06acfe",null);t["a"]=m.exports},8246:function(e,t,a){"use strict";a("8c45")},"8b49":function(e,t,a){"use strict";a("0970")},"8c45":function(e,t,a){},ae8d:function(e,t,a){"use strict";var r=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"card"},[e.title?a("div",{staticClass:"card-head"},[e._v(" "+e._s(e.title)+" "),a("div",{staticStyle:{float:"right"}},[e._t("op")],2)]):e._e(),e._t("default")],2)},n=[],o={name:"Card",props:{title:{type:String,default:""}}},s=o,i=(a("e24e"),a("52e0")),l=Object(i["a"])(s,r,n,!1,null,"88a16ad0",null);t["a"]=l.exports},c4b0:function(e,t,a){"use strict";a.r(t);var r=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("transition-group",{staticClass:"grid-rows",attrs:{tag:"div",appear:""},on:{"before-enter":e.beforeEnter,enter:e.animEnter}},[a("Mine",{key:"0",attrs:{"data-index":"0.3"}}),a("Upload",{key:"1",attrs:{"data-index":"0"}})],1)},n=[],o=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("Card",{attrs:{title:"我的文件"}},[a("FilesTable",{attrs:{files:e.files}})],1)},s=[],i=a("ae8d"),l=a("752a"),c=a("604e"),u=a("07a4"),f=a("eeef"),d=a("f778"),m={name:"Mine",mixins:[f["a"],d["a"]],components:{Card:i["a"],FilesTable:l["a"]},data:function(){return{files:[],bookmark:""}},mounted:function(){var e=this,t="",a=u["a"].userName(),r=this.bookmark;c["a"].files({userName:a,tag:t,bookmark:r}).then((function(t){e.files=t.contents,e.bookmark=t.bookmark})).catch(console.log)}},p=m,h=(a("8246"),a("52e0")),b=Object(h["a"])(p,o,s,!1,null,"39e264c4",null),g=b.exports,v=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("Card",{attrs:{title:"上传文件"}},[a("div",{staticClass:"grid-cols"},[a("el-form",{ref:"uploadForm",attrs:{"label-position":"left",rules:e.uploadRules,model:e.form,"label-width":"100px"}},[a("el-form-item",{attrs:{prop:"file",label:"* 选择文件"}},[a("input",{attrs:{type:"file",id:"input-file"}})]),a("el-form-item",{attrs:{prop:"policy",label:"策略表达式"}},[a("el-input",{attrs:{placeholder:"(A AND B AND (C OR D))"},model:{value:e.form.policy,callback:function(t){e.$set(e.form,"policy",t)},expression:"form.policy"}})],1),a("el-form-item",{attrs:{prop:"tags",label:"标签"}},[a("el-input",{attrs:{placeholder:"城市 系统 业务 备注（空格隔开）"},model:{value:e.form.tags,callback:function(t){e.$set(e.form,"tags",t)},expression:"form.tags"}})],1),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:e.upload}},[e._v("上传到服务器")])],1)],1)],1)])},w=[],y=a("723b"),k=(a("ac30"),a("f6a5"),a("d065"),a("70f6")),N={name:"Upload",components:{Card:i["a"]},data:function(){return{form:{tags:"",policy:""},options:["shanghai","myc","edu","test"],uploadRules:{policy:[{required:!0,trigger:"blur",message:"请填写上传策略"}],tags:[{required:!0,trigger:"blur",message:"请设置标签"}]}}},watch:{},mounted:function(){return Object(y["a"])(regeneratorRuntime.mark((function e(){return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:case"end":return e.stop()}}),e)})))()},methods:{selecedFile:function(e){this.file=e.file},handleRemove:function(e){this.$refs.upload.abort(e)},upload:function(){var e=this,t=document.querySelector("input[type=file]").files[0];if(console.log(t),t){var a=this.form.tags.split(" ");a.length<3?k["Notification"].error({title:"拒绝",message:"请补充完整的标签",duration:2e3}):this.$refs.uploadForm.validate((function(r){if(r){var n=u["a"].userName(),o=e.form.policy;c["a"].encrypt({file:t,userName:n,tags:a,policy:o}).then((function(e){Object(k["Message"])({message:"上传成功",duration:5e3,type:"success"}),console.log(e)})).catch((function(e){Object(k["Message"])({message:e.message,duration:5e3,type:"error"})}))}}))}else k["Notification"].error({title:"拒绝",message:"请添加文件",duration:2e3})}}},_=N,U=(a("8b49"),Object(h["a"])(_,v,w,!1,null,"970053c8",null)),x=U.exports,C={name:"Files",components:{Mine:g,Upload:x},methods:{beforeEnter:function(e){e.dataset.index>-1&&(e.style.opacity=.5,e.style.transform="translateX(30px)")},animEnter:function(e,t){var a=250*e.dataset.index;setTimeout((function(){e.style="",t()}),a)}}},j=C,O=(a("f723"),Object(h["a"])(j,r,n,!1,null,"f30e4408",null));t["default"]=O.exports},de87:function(e,t,a){},e24e:function(e,t,a){"use strict";a("e826")},e826:function(e,t,a){},eeef:function(e,t,a){"use strict";a.d(t,"a",(function(){return r}));a("0721"),a("7fa7"),a("042e"),a("c189"),a("3435");var r={methods:{saveFile:function(e,t){var a=window.URL.createObjectURL(new Blob([t])),r=document.createElement("a");r.href=a,r.setAttribute("download",e),document.body.appendChild(r),r.click()}}}},f723:function(e,t,a){"use strict";a("de87")},f778:function(e,t,a){"use strict";a.d(t,"a",(function(){return r}));a("5cf2"),a("0721"),a("ac30"),a("f6a5"),a("8281");var r={methods:{filterEmpty:function(e){if(void 0==e)return"";if(Array.isArray(e)){var t=e.join(" ").split(" ").filter((function(e){return e}));return t}return e}}}}}]);
//# sourceMappingURL=chunk-47ff02e3.e8ff3b95.js.map