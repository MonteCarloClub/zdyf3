import { defineStore } from "pinia";
import { Nullable, Undefable } from "@/utils/types";
import { TOKEN_KEY } from "@/common/constants";
import { setStorage } from "@/utils/storage";

export interface UserInfo {
  userId: string | number;
  username: string;
  avatar?: string;
  desc?: string;
  homePath?: string;
}

interface UserState {
  userInfo: Nullable<UserInfo>;
  token?: string;
}

export const useUserStore = defineStore({
  id: "app-user",
  state: (): UserState => ({
    userInfo: null,
    token: undefined,
  }),
  getters: {
    getUserInfo(state): Nullable<UserInfo> {
      return state.userInfo;
    },
    getToken(state): Undefable<string> {
      return state.token;
    },
  },
  actions: {
    setToken(token: Undefable<string>) {
      this.token = token ? token : ""; // for null or undefined value
      setStorage(TOKEN_KEY, token)
    },
    setUserInfo(info: Nullable<UserInfo>) {
      this.userInfo = info;
    },
    resetState() {
      this.userInfo = null;
      this.token = "";
    },
    /**
     * @description: login
     */
    async login(): Promise<Nullable<UserInfo>> {
      try {
        const token = "demo-token";
        // save token
        this.setToken(token);
        return this.afterLoginAction();
      } catch (error) {
        return Promise.reject(error);
      }
    },
    async afterLoginAction(): Promise<Nullable<UserInfo>> {
      if (!this.getToken) return null;
      // get user info
      const userInfo = await this.getUserInfoAction();
      return userInfo;
    },
    async getUserInfoAction(): Promise<Nullable<UserInfo>> {
      if (!this.getToken) return null;
      const userInfo: UserInfo = {
        userId: 123,
        username: "kris",
      };
      this.setUserInfo(userInfo);
      return userInfo;
    },
    /**
     * @description: logout
     */
    async logout() {
      if (this.getToken) {
        try {
          // await doLogout();
        } catch {
          console.log("注销Token失败");
        }
      }
      this.setToken(undefined);
      this.setUserInfo(null);
    },
  },
});

// Need to be used outside the setup
// export function useUserStoreWithOut() {
//   return useUserStore(store);
// }
