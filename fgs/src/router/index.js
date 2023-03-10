import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/home/Home.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      headerClass: "transparent-header" // 主页的导航栏透明
    }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
  },
  {
    path: '/signup',
    name: 'signup',
    component: () => import('../views/SignUp.vue'),
  },
  {
    path: '/allfiles',
    name: 'allfiles',
    component: () => import('../views/AllFiles.vue'),
  },
  {
    path: '/user',
    name: 'user',
    component: () => import('../views/User.vue'),
    children: [
      {
        path: '',
        component: () => import('../views/user/User.vue'),
      },
      {
        path: '/attr',
        component: () => import('../views/attributes/_Mine.vue'),
      },
      {
        path: '/attr/application',
        component: () => import('../views/attributes/_Applications.vue'),
      },
      {
        path: '/attr/approve',
        component: () => import('../views/attributes/_Approvals.vue'),
      },
      {
        path: '/attr/history',
        component: () => import('../views/attributes/_History.vue'),
      },
      {
        path: '/files',
        component: () => import('../views/files/_Mine.vue'),
      },
      {
        path: '/files/upload',
        component: () => import('../views/files/_Upload.vue'),
      },
      {
        path: '/organization/:org',
        component: () => import('../views/organization/Organization.vue'),
      },
    ]
  },
  
  // {
  //   path: '/user',
  //   name: 'user',
  //   component: () => import('../views/user/User.vue'),
  //   children: [
  //     {
  //       path: '',
  //       name: 'attributes',
  //       component: () => import('../views/user/attributes/Attributes.vue'),
  //     },
  //     {
  //       path: 'files',
  //       name: 'files',
  //       component: () => import('../views/user/files/Files.vue'),
  //     },
  //     {
  //       path: 'organizations',
  //       name: 'organizations',
  //       component: () => import('../views/user/organizations/Organizations.vue'),
  //     },
  //     {
  //       path: 'organization/:org',
  //       name: 'organization',
  //       component: () => import('../views/user/organization/Organization.vue'),
  //     },
  //   ]
  // },
]

const router = new VueRouter({
  routes
})

export default router
