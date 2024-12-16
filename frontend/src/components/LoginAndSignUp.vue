
<template>
  <div class="auth-wrapper">
    <div class="background-animation"></div>
    <div class="auth-inner">
      <div class="auth-container">
        <!-- 左侧图片 -->
        <div class="auth-image">
          <img src="@/assets/loginPhoto.jpg" alt="Login Image" />
        </div>

        <div class="auth-form">
          <div v-if="isLogin">
            <form @submit.prevent="handleLogin">
              <h3>Login</h3>

              <div class="form-group">
                <label>Username</label>
                <input
                    type="text"
                    class="form-control"
                    placeholder="Username"
                    v-model="loginForm.username"
                />
              </div>

              <div class="form-group">
                <label>Password</label>
                <div class="input-group">
                  <input
                      :type="loginForm.showPassword ? 'text' : 'password'"
                      class="form-control"
                      placeholder="Password"
                      v-model="loginForm.password"
                  />
                  <div class="input-group-append">
                    <span class="input-group-text" @click="togglePasswordVisibility('login')">
                      <font-awesome-icon :icon="loginForm.showPassword ? 'eye-slash' : 'eye'" />
                    </span>
                  </div>
                </div>
              </div>

              <div class="form-group form-check">
                <input
                    type="checkbox"
                    class="form-check-input"
                    id="autoLogin"
                    v-model="loginForm.autoLogin"
                />
                <label class="form-check-label" for="autoLogin">Auto Login</label>
              </div>

              <div class="d-flex justify-content-center">
                <button type="submit" class="btn btn-primary">Login</button>
              </div>

              <!--切换-->
              <div class="text-center mt-3">
                <span>No an account yet？</span>
                <a href="#" @click.prevent="toggleForm">Sign Up Here</a>
              </div>
            </form>
          </div>

          <!--  isLogin = False-->
          <div v-else>
            <form @submit.prevent="handleSignUp">
              <h3>Sign Up</h3>

              <div class="form-group">
                <label>Username</label>
                <input
                    type="text"
                    class="form-control"
                    placeholder="Username"
                    v-model="signUpForm.username"
                />
              </div>

              <div class="form-group">
                <label>mail</label>
                <input
                    type="mail"
                    class="form-control"
                    placeholder="mail"
                    v-model="signUpForm.mail"
                    @blur="validatmail"
                />
                <div v-if="signUpForm.mailError" class="error-message">
                  {{ signUpForm.mailError }}
                </div>
              </div>

              <div class="form-group">
                <label>Password</label>
                <div class="input-group">
                  <input
                      :type="signUpForm.showPassword ? 'text' : 'password'"
                      class="form-control"
                      placeholder="Password"
                      v-model="signUpForm.password"
                  />
                  <div class="input-group-append">
                    <span class="input-group-text" @click="togglePasswordVisibility('signUp')">
                      <font-awesome-icon :icon="signUpForm.showPassword ? 'eye-slash' : 'eye'" />
                    </span>
                  </div>
                </div>
              </div>

              <div class="form-group">
                <label>Confirm Password</label>
                <div class="input-group">
                  <input
                      :type="signUpForm.showConfirmPassword ? 'text' : 'password'"
                      class="form-control"
                      placeholder="Confirm Password"
                      v-model="signUpForm.confirmPassword"
                  />
                  <div class="input-group-append">
                    <span class="input-group-text" @click="toggleConfirmPasswordVisibility">
                      <font-awesome-icon :icon="signUpForm.showConfirmPassword ? 'eye-slash' : 'eye'" />
                    </span>
                  </div>
                </div>
              </div>

              <div class="form-group">
                <label>Verification Code</label>
                <input
                    type="password"
                    class="form-control"
                    v-model="signUpForm.verificationCode"
                />
              </div>

              <div class="form-group">
                <button
                    type="button"
                    class="btn btn-secondary"
                    @click="sendVerificationCode"
                    :disabled="signUpForm.isSendingCode"
                >
                  {{
                    signUpForm.isSendingCode
                        ? `Resend in ${signUpForm.countdown}s`
                        : "Send Verification Code"
                  }}
                </button>
              </div>

              <button type="submit" class="btn btn-primary btn-block">
                Sign Up
              </button>

              <div class="text-center mt-3">
                <span>Already have an account？</span>
                <a href="#" @click.prevent="toggleForm">Login Here</a>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { inject } from "vue";
export default {
  name: "AuthPage",
  data() {
    return {
      headers: inject("headers"),
      isLogin: true,
      loginForm: {
        username: "",
        password: "",
        autoLogin: false,
        showPassword: false,
      },
      signUpForm: {
        username: "",
        mail: "",
        password: "",
        confirmPassword: "",
        verificationCode: "",
        mailError: "",
        isSendingCode: false,
        countdown: 60,
        showPassword: false,
        showConfirmPassword: false,
      },
    };
  },
  methods: {
    toggleForm() {
      //切换值
      this.isLogin = !this.isLogin;
    },
    validatmail() {
      const mailPattern = /^2.*@buaa\.edu\.cn$/;
      if (!mailPattern.test(this.signUpForm.mail)) {
        this.signUpForm.mailError =
            'mail must start with "2" and end with "buaa.edu.cn".';
      } else {
        this.signUpForm.mailError = "";
      }
    },
    async sendVerificationCode() {
      if (this.signUpForm.mailError || !this.signUpForm.mail) {
        return;
      }

      this.signUpForm.isSendingCode = true;
      this.signUpForm.countdown = 60;

      const interval = setInterval(() => {
        this.signUpForm.countdown--;
        if (this.signUpForm.countdown <= 0) {
          clearInterval(interval);
          this.signUpForm.isSendingCode = false;
        }
      }, 1000);

      const mail = this.signUpForm.mail;

      try {
        // send verification to backend
        const response = await fetch(`/api/short-link/user/send-code?mail=${encodeURIComponent(mail)}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        });

        const data = await response.json();

        if (response.ok) {
          // verification request success
          alert("Verification code request successfully!");
        } else {
          // verification request failed
          alert(`Failed to request verification code: ${data.message}`);
        }
      } catch (error) {
        console.error("Error during sending verification code:", error);
        alert(
            "An error occurred while sending the verification code. Please try again later."
        );
      }
    },
    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        alert("Please fill in all fields.");
        return;
      }

      const requestData = {
        username: this.loginForm.username,
        password: this.loginForm.password,
      };

      try {
        // send login request
        const response = await fetch("/api/short-link/user/login", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(requestData),
        });

        const data = await response.json();

        if (response.ok) {
          console.log(data);
          this.headers.username = this.loginForm.username;
          this.headers.token = data.data.token;
          // login success
          alert("Login successful!");
          // login success jump to home page
          this.$router.push("/home");
        } else {
          // login failed
          alert(`Login failed: ${data.message}`);
        }
      } catch (error) {
        console.error("Error during login:", error);
        alert("An error occurred during login. Please try again later.");
      }
    },
    async handleSignUp() {
      if (
          !this.signUpForm.username ||
          !this.signUpForm.mail ||
          !this.signUpForm.password ||
          !this.signUpForm.confirmPassword ||
          !this.signUpForm.verificationCode
      ) {
        alert("Please fill in all fields.");
        return;
      }

      if (this.signUpForm.password !== this.signUpForm.confirmPassword) {
        alert("Passwords do not match.");
        return;
      }

      if (this.signUpForm.mailError) {
        alert("Invalid mail format.");
        return;
      }

      const requestData = {
        username: this.signUpForm.username,
        mail: this.signUpForm.mail,
        password: this.signUpForm.password,
        verificationCode: this.signUpForm.verificationCode,
      };

      try {
        // send register request
        const response = await fetch("/api/short-link/user", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(requestData),
        });

        const data = await response.json();

        if (response.ok) {
          // register success
          alert("Registration successful!");
          this.toggleForm(); // 切换到登录
        } else {
          // register failed
          alert(`Registration failed: ${data.message}`);
        }
      } catch (error) {
        console.error("Error during registration:", error);
        alert("An error occurred during registration. Please try again later.");
      }
    },
    togglePasswordVisibility(formType) {
      if (formType === 'login') {
        this.loginForm.showPassword = !this.loginForm.showPassword;
      } else if (formType === 'signUp') {
        this.signUpForm.showPassword = !this.signUpForm.showPassword;
      }
    },
    toggleConfirmPasswordVisibility() {
      this.signUpForm.showConfirmPassword = !this.signUpForm.showConfirmPassword;
    },
  },
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css?family=Fira + Sans:400,500,600,700.800");

* {
  box-sizing: border-box;
}

.auth-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  position: relative; /* 确保背景动画容器定位正确 */
}

.auth-inner {
  width: 800px;
  margin: auto;
  background: rgba(255, 255, 255, 0.95); /* 增强透明度 */
  box-shadow: 0px 14px 80px rgba(34, 35, 58, 0.2);
  padding: 0; /* 去掉内边距 */
  border-radius: 15px;
  transition: all 0.3s;
  position: relative; /* 确保内容在背景动画之上 */
  z-index: 1; /* 确保内容在背景动画之上 */
  display: flex;
}

.auth-container {
  display: flex;
  width: 100%;
}

.auth-image {
  width: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  border-radius: 15px 0 0 15px;
}

.auth-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.auth-form {
  width: 50%;
  padding: 40px 55px 45px 55px;
}

.auth-wrapper h3 {
  text-align: center;
  margin: 0;
  line-height: 1;
  padding-bottom: 20px;
}

.error-message {
  color: red;
  font-size: 0.875rem;
}

.input-group-text {
  cursor: pointer;
}

/* 背景动画样式 */
.background-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, #030303, #1183a6, #167168, #87cefa);
  background-size: 400% 400%;
  animation: gradientAnimation 15s ease infinite;
}

@keyframes gradientAnimation {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
</style>