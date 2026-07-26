<script setup lang="ts">

import type {EventOption} from "../types";
import { ref, computed } from "vue";

const props = defineProps<{ eventOptions: EventOption[] }>()

const emit = defineEmits(['createResult'])

const athleteName = ref<string>('')
const event = ref<string>('')
const performanceValue = ref<number | null>(null)
const resultDate = ref<string>('')

const unitForEvent = computed(() => {
  return props.eventOptions.find(e => e.event == event.value)?.unit.toLowerCase()
})

const placeHolderText = computed(() => {
  return event.value ? `Use ${ unitForEvent.value}` : "Enter a value"
})

function handleSubmit() {
  const payLoad = {
    athleteName: athleteName.value,
    event: event.value,
    performanceValue: performanceValue.value,
    resultDate: resultDate.value
  }
  console.log(payLoad)
  emit('createResult', payLoad)
}

</script>

<template>
  <form @submit.prevent="handleSubmit">
    <label for="name">Athlete's name: </label>
    <input type="text" id="name" v-model="athleteName" required>

    <label for="events">Selected: </label>
    <select name="events" id="events" v-model="event" required>
      <option disabled value="">Please select one</option>
      <option
          v-for="eventOption in eventOptions"
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
    >

    <label for="date">Date:</label>
    <input type="date" id="date" name="date" v-model="resultDate" required>

    <input type="submit" value="Submit">
  </form>
</template>

<style>

</style>