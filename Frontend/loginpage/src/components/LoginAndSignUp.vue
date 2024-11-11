// 登录和注册页面

<template>
  <div class="auth-wrapper">
    <div class="auth-inner">

      <div v-if="isLogin">
        <form @submit.prevent="handleLogin">
          <h3>Login</h3>

          <div class="form-group">
            <label>Username</label>
            <input type="text" class="form-control" placeholder="Username" v-model="loginForm.username"/>
          </div>

          <div class="form-group">
            <label>Password</label>
            <input type="password" class="form-control" placeholder="Password" v-model="loginForm.password"/>
          </div>

          <div class="form-group form-check">
            <input type="checkbox" class="form-check-input" id="autoLogin" v-model="loginForm.autoLogin"/>
            <label class="form-check-label" for="autoLogin">Auto Login</label>
          </div>

          <div class="form-group text-right">
            <a href="#" class="forgot-password">Forgot Password？</a>
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
            <input type="text" class="form-control" placeholder="Username" v-model="signUpForm.username"/>
          </div>

          <div class="form-group">
            <label>Email</label>
            <input type="email" class="form-control" placeholder="Email" v-model="signUpForm.email" @blur="validateEmail"/>
            <div v-if="signUpForm.emailError" class="error-message">{{ signUpForm.emailError }}</div>
          </div>

          <div class="form-group">
            <label>Password</label>
            <input type="password" class="form-control" placeholder="Password" v-model="signUpForm.password"/>
          </div>

          <div class="form-group">
            <label>Confirm Password</label>
            <input type="password" class="form-control" placeholder="Confirm Password" v-model="signUpForm.confirmPassword"/>
          </div>

          <div class="form-group">
            <button type="button" class="btn btn-secondary" @click="sendVerificationCode" :disabled="signUpForm.isSendingCode">
              {{ signUpForm.isSendingCode ? `Resend in ${signUpForm.countdown}s` : 'Send Verification Code' }}
            </button>
          </div>

          <button type="submit" class="btn btn-primary btn-block">Sign Up</button>

          <div class="text-center mt-3">
            <span>Already have an account？</span>
            <a href="#" @click.prevent="toggleForm">Login Here</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AuthPage',
  data() {
    return {
      isLogin: true,
      loginForm: {
        username: '',
        password: '',
        autoLogin: false,
      },
      signUpForm: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        emailError: '',
        isSendingCode: false,
        countdown: 60,
      },
    };
  },
  methods: {
    toggleForm() {  //切换值
      this.isLogin = !this.isLogin;
    },
    validateEmail() {
      const emailPattern = /^2.*@buaa\.edu\.cn$/;
      if (!emailPattern.test(this.signUpForm.email)) {
        this.signUpForm.emailError = 'Email must start with "2" and end with "buaa.edu.cn".';
      } else {
        this.signUpForm.emailError = '';
      }
    },
    sendVerificationCode() {
      if (this.signUpForm.emailError || !this.signUpForm.email) {
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

      console.log('Sending verification code to:', this.signUpForm.email);
    },
    async handleLogin() {
      console.log('Login form submitted:', this.loginForm);
    },
    async handleSignUp() {
      console.log('SignUp form submitted:', this.signUpForm);
    },
  },
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css?family=Fira + Sans:400,500,600,700.800');

* {
  box-sizing: border-box;
}

.auth-wrapper {
  display: flex;
  justify-content: center;
  flex-direction: column;
  text-align: left;
  height: 100vh;
}

.auth-inner {
  width: 450px;
  margin: auto;
  background: #ffffff;
  box-shadow: 0px 14px 80px rgba(34, 35, 58, 0.2);
  padding: 40px 55px 45px 55px;
  border-radius: 15px;
  transition: all .3s;
}

.auth-wrapper h3 {
  text-align : center;
  margin : 0;
  line-height: 1;
  padding-bottom: 20px;
}

.error-message {
  color: red;
  font-size: 0.875rem;
}
</style>