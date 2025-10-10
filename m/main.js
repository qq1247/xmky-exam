// #ifndef VUE3
import Vue from 'vue';
import App from './App';

Vue.config.productionTip = false;

App.mpType = 'app';

const app = new Vue({
	...App
});
app.$mount();
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue';
import * as Pinia from 'pinia';
import { createUnistorage } from './uni_modules/pinia-plugin-unistorage';
import App from './App.vue';
export function createApp() {
	const app = createSSRApp(App);
	const store = Pinia.createPinia();
	store.use(createUnistorage());
	app.use(store);

	return {
		app,
		Pinia
	};
}
// #endif

// #ifdef H5
if (process.env.NODE_ENV === 'development') {
	// import('vconsole').then(({ default: VConsole }) => {
	//   new VConsole()
	// })
}
// #endif
