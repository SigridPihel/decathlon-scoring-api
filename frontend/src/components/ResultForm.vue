<script setup lang="ts">

import type {Event} from "../types";
import {computed, ref} from "vue";

const props = defineProps<{ events: Event[] }>()

const emit = defineEmits(['createResult'])

const athleteName = ref<string>('')
const event = ref<string>('')
const performanceValue = ref<number | null>(null)
const resultDate = ref<string>('')

const unitForEvent = computed(() => {
  return props.events.find(e => e.event == event.value)?.unit.toLowerCase()
})

const placeHolderText = computed(() => {
  return event.value ? `Use ${ unitForEvent.value}` : "Enter a value"
})

const todayDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear().toString()
  const month = (now.getMonth() + 1) > 9 ? (now.getMonth() + 1).toString() : '0' + (now.getMonth() + 1).toString()
  const day = now.getDate() > 9 ? now.getDate().toString() : '0' + now.getDate().toString()
  return `${year}-${month}-${day}`
})

function handleSubmit() {
  const payLoad = {
    athleteName: athleteName.value,
    event: event.value,
    performanceValue: performanceValue.value,
    resultDate: resultDate.value
  }
  emit('createResult', payLoad)
}

</script>

<template>
  <form @submit.prevent="handleSubmit">
    <label for="name">Athlete's name: </label>
    <input type="text" id="name" v-model="athleteName" required>

    <label for="events">Selected: </label>
    <select name="events" id="events" v-model="event" required>
      <option disabled value="">Select event</option>
      <option
          v-for="eventOption in events"
          :key="eventOption.event"
          :value="eventOption.event"
      >
        {{ eventOption.displayName }}
      </option>
    </select>

    <label for="performanceValue">Score: </label>
    <input
        type="number"
        id="performanceValue"
        step="any"
        :placeholder="placeHolderText"
        v-model="performanceValue"
        required
        min="0.01"
    >

    <label for="date">Date:</label>
    <input
        type="date"
        id="date"
        name="date"
        v-model="resultDate"
        required
        :max="todayDate"
    >

    <input type="submit" value="Submit">
  </form>
</template>

<style scoped>
form {
  display: flex;
  flex-direction: column;
}

</style>