const promise = new Promise((resolve, reject)=>{
    num = Math.round(Math.random() * 10);
    if(num >= 4)
        resolve(`Success: ${num} is greater than 4`);
    else
        reject(`Failure: ${num} is lesser than 4`);
});
promise
.then((msg)=>console.log(msg))
.catch((errMsg)=>console.log(errMsg));