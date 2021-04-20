import React from 'react'
import { Provider } from 'react-redux'
import { createStore } from 'redux'

import './styles.css'

import SendEmailForm from './SendEmailForm.js'

import reducer from './reducer'
import { AppBar, Container, Toolbar, Typography } from '@material-ui/core'

const store = createStore(reducer)

function App() {
  return (
    <Provider store={store}>
      <AppBar position="static">
        <Toolbar variant="dense">
          <Typography variant="h6" color="inherit">
            Email Scheduler
          </Typography>
        </Toolbar>
      </AppBar>
      <div style={{ marginTop: '20px' }}></div>
      <Container maxWidth="sm">
        <Typography variant="h4" component="h4">
          Schedule Email
        </Typography>
        <SendEmailForm />
      </Container>
    </Provider>
  )
}

export default App
