module.exports = {
	presets: ['@vue/cli-plugin-babel/preset'],
	plugins: [
		[
			'prismjs',
			{
				languages: 'all',
				plugins: ['match-braces', 'line-numbers', 'copy-to-clipboard'],
				theme: 'tomorrow',
				css: true
			}
		]
	]
};
