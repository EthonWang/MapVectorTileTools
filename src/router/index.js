/*
 * @Author: wyjq
 * @Date: 2021-11-04 14:55:33
 * @Description: 
 */

import VueRouter from 'vue-router'

import HomePage from "../pages/Home/HomePage"
import Page1 from "../pages/page1/Page1"
import Page2 from "../pages/page2/Page2"

export default new VueRouter({
	routes: [
		{
			path: '/',
			redirect: '/home'
		},
		{
			path: "/home",
			component: HomePage,
			redirect: '/home/page1',
			children: [
				{
					path: 'page1',
					component: Page1
				},
				{
					path: 'page2',
					component: Page2
				},
			]
		},

	]
})