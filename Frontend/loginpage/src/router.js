import {createRouter, createWebHistory} from "vue-router";
import HomePage from './components/Home.vue';
import LoginAndSignUp from "@/components/LoginAndSignUp.vue";

const routes = [
    { path: '', component: LoginAndSignUp },
    { path: '/home', component: HomePage },

]

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;