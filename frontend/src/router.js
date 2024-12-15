import {createRouter, createWebHistory} from "vue-router";
import LoginAndSignUp from "@/components/LoginAndSignUp.vue";
import Home from "./components/Home.vue";
import RecycleBin from "./components/recycleBin.vue";
import CreateShortLinkCopy from "./components/createShortLink copy.vue";
import UploadPhoto from "./components/UploadPhoto.vue";
import GlobalDialog from "./components/GlobalDialog.vue";

const routes = [
    { path: '', component: LoginAndSignUp },
    { path: '/home', component: Home },
    { path: '/createShortLink', component: CreateShortLinkCopy },
    { path: '/recycleBin', component: RecycleBin },
    { path: '/photo', component: UploadPhoto },
    { path: '/quickStart', component:GlobalDialog}
]

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;