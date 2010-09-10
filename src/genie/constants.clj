(ns genie.constants)

(def responses
     {:user-exists
      "A user with that username already exists. Please choose another."
      :invalid-characters
      "There were invalid characters in your input. Remember that your username and password must be 3 to 12 characters long, and contain only alphanumeric characters and underscores."
      :registration-success
      "Registration successful! Now check your mail to validate your account."
      :unvalidated
      "Before you can log in, you must first validate your account."
      :user-not-found
      "A user with that username was not found. Remember that your username and password is case-sensitive."
      :incorrect-password
      "You entered an incorrect password. Remember that your password is case-sensitive."
      :login-success
      "You've successfully logged in!"})
