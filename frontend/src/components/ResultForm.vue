<script setup lang="ts">

import type {Event} from "../types";
import {computed, ref} from "vue";

const props = defineProps<{ events: Event[] }>()

const emit = defineEmits(['createResult', 'close'])

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
  <h2>Add Result</h2>
  <button class="close" type="button" @click="emit('close')">X</button>
  <form @submit.prevent="handleSubmit">
    <div class="form-element">
      <label for="name">Athlete's name: </label>
      <input type="text" id="name" v-model="athleteName" required>
    </div>

    <div class="form-element">
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
    </div>

    <div class="form-element">
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
    </div>

    <div class="form-element">
      <label for="date">Date:</label>
      <input
          type="date"
          id="date"
          name="date"
          v-model="resultDate"
          required
          :max="todayDate"
      >
    </div>

    <div class="form-element submit">
      <input type="submit" value="Submit">
    </div>
  </form>
</template>

<style scoped>
form {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 10px;
}

.form-element {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 10px;
  padding: 10px;
}


.close {
  all: unset;
  cursor: pointer;
  top: 15px;
  right: 15px;
  position: absolute;
  background-color: lightgray;
  padding: 5px;
  border-radius: 15%;
  color: white;
}

h2 {
  text-align: center;
}
</style>