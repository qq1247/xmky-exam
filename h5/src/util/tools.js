import { Message } from "element-ui";
const message = (msg, type = "success", date = 1500) => {
  Message({
    message: msg,
    duration: date,
    type: type
  })
};

export { message }
