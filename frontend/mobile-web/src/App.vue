<template>
  <div :class="['mobile-app', { 'has-tabbar': route.meta.showTab }]">
    <router-view />
    <TabBar v-if="route.meta.showTab" />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import TabBar from './components/TabBar.vue'

const route = useRoute()
</script>
