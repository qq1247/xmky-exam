import { createSSRApp } from 'vue'
import * as Pinia from 'pinia'
import { createUnistorage } from './uni_modules/pinia-plugin-unistorage'
import App from './App.vue'
// #ifdef H5
//import VConsole from 'vconsole';
// #endif

export function createApp() {
	const app = createSSRApp(App)
	const store = Pinia.createPinia()
	store.use(createUnistorage())
	app.use(store)
	
	// #ifdef H5
	//const vConsole = new VConsole();
	// #endif
	
	return { app, Pinia }
}