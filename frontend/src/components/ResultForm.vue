<script setup lang="ts">

import type {EventOption} from "../types";
import {ref} from "vue";

defineProps<{ eventOptions: EventOption[] }>()

const athleteName = ref<string>('')
const event = ref<string>('')
const performanceValue = ref<number | null>(null)
const resultDate = ref<string>('')

</script>

<template>
  <form>
    <label for="name">Athlete's name: </label>
    <input type="text" id="name" v-model="athleteName" required>


  </form>
</template>

<style>

</style>