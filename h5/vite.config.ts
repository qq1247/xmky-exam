import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { prismjsPlugin } from 'vite-plugin-prismjs'

// https://vite.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        vueJsx(), ,
        prismjsPlugin({
            languages: 'all',
            plugins: ['match-braces', 'line-numbers', 'copy-to-clipboard'],
            theme: 'tomorrow',
            css: true
        })
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
    server: {
        host: '0.0.0.0',
        port: 9000,
    },
})
