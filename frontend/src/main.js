import { createApp, reactive, ref } from 'vue'
import App from './App.vue'
import router from "@/router";
import 'bootstrap/dist/css/bootstrap.min.css';

const headers = reactive({
  username: "",
  token: "",
} );
const isVip = ref(false);
const app = createApp( App );
const gid = ref( "gid" );
const name = ref("");
app.use(router);
app.mount('#app')
app.provide( 'headers', headers );
app.provide( 'isVip', isVip );
app.provide( "gid", gid );
app.provide("name", name);