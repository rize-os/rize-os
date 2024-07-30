/** @type {import('tailwindcss').Config} */
export default {
  content: ["src/main/frontend/index.html", "src/main/frontend/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: {
          100: '#006AF51A',
          900: '#006af5'
        },

        'primary-text': '#005fdb'
      }
    },
  },
  plugins: [],
};