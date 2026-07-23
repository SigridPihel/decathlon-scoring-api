export interface Result {
    id: string
    resultDate: string
    athleteName: string
    event: string
    performanceValue: number
    points: number
    unit: string
}

export interface EventOption {
    event: string
    displayName: string
    unit: string
}