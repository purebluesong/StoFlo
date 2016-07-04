export const isemail = x => /^[A-Za-zd]+([-_.][A-Za-zd]+)*@([A-Za-zd]+[-.])+[A-Za-zd]{2,5}$/.test(x)
export const ispassowrd = x => x.length >= 8 && /\d/.test(x) && /[a-zA-Z]/.test(x)
