const initialState = {
  formValues: {},
  messageType: '',
  messageText: '',
}

export const MessageType = {
  success: 'success',
  failed: 'error',
}
const reducer = (state = initialState, action) => {
  switch (action.type) {
    case 'SET_FORM_VALUES':
      return {
        ...state,
        formValues: action.payload,
      }
    case 'SUBMIT_FORM':
      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ ...state.formValues }),
      }
      fetch(
        `${process.env.REACT_APP_BACK_END_BASE_URL}/schedule-email`,
        requestOptions,
      )
        .then((response) => {
          return response.json()
        })
        .then((data) => {
          if (data.success === false) {
            action.dispatch({
              type: 'FAILED_REQUEST',
              ...state,
            })
          } else if (data.success === true) {
            action.dispatch({
              type: 'SUCCESS_REQUEST',
              payload: {
                ...state,
              },
            })
          }
        })
        .catch((error) => console.log(error))

      return {
        ...state,
      }
    case 'FAILED_REQUEST':
      return {
        ...state,
        messageText: `Failed to scheduled for ${state.formValues.dateTime} (${state.formValues.timeZone})`,
        messageType: MessageType.failed,
      }
    case 'SUCCESS_REQUEST':
      return {
        ...state,
        messageText: `Email Scheduled for ${state.formValues.dateTime} (${state.formValues.timeZone})`,
        messageType: MessageType.success,
      }
    default:
      return state
  }
}

export default reducer
