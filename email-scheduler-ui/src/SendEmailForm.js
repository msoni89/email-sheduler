import { Button, TextField } from '@material-ui/core'
import Alert from '@material-ui/lab/Alert'

import React, { Component } from 'react'
import { connect } from 'react-redux'
import moment from 'moment'

class SendEmailForm extends Component {
  constructor(props) {
    super(props)
    this.state = {
      values: {
        timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone,
        dateTime: moment().add(1, 'hours').format('YYYY-MM-DDTHH:mm:ss'),
      },
    }
  }

  submitForm = (e) => {
    e.preventDefault()

    this.props.dispatch({
      type: 'SUBMIT_FORM',
      dispatch: this.props.dispatch,
    })
  }

  handleInputChange = (e) =>
    this.setState(
      {
        values: {
          ...this.state.values,
          [e.target?.name]: e.target?.value,
        },
      },
      () =>
        this.props.dispatch({
          type: 'SET_FORM_VALUES',
          payload: this.state.values,
        }),
    )

  render() {
    return (
      <div style={{ marginTop: '20px' }}>
        <form onSubmit={this.submitForm} autoComplete="off">
          <div className="input-group">
            <TextField
              required
              type="email"
              name="email"
              id="email"
              label="Email address"
              defaultValue={this.state.values.email}
              onChange={this.handleInputChange}
              variant="outlined"
            />
          </div>

          <div className="input-group">
            <TextField
              required
              type="subject"
              name="subject"
              id="subject"
              label="Subject"
              defaultValue={this.state.values.subject}
              onChange={this.handleInputChange}
              variant="outlined"
            />
          </div>
          <div className="input-group">
            <TextField
              multiline
              required
              type="text"
              name="body"
              id="body"
              label="Body"
              rows={4}
              defaultValue={this.state.values.body}
              onChange={this.handleInputChange}
              variant="outlined"
            />
          </div>
          <div className="input-group">
            <TextField
              id="datetime-local"
              disabled
              name="dateTime"
              id="date-time"
              label="Schedule For"
              type="datetime-local"
              defaultValue={this.state.values.dateTime}
              onChange={this.handleInputChange}
              InputLabelProps={{
                shrink: true,
              }}
            />
          </div>

          <div className="input-group">
            <label htmlFor="timeZone">{this.state.values.timeZone}</label>
          </div>
          <Button type="submit" variant="contained" color="primary">
            Schedule
          </Button>
        </form>

        <div className="message">
          {this.props.messageText?.length > 0 && (
            <Alert variant="outlined" severity={this.props.messageType}>
              {this.props.messageText}
            </Alert>
          )}
        </div>
      </div>
    )
  }
}

const mapStateToProps = (state) => ({
  messageText: state.messageText,
  messageType: state.messageType,
})

export default connect(mapStateToProps)(SendEmailForm)
