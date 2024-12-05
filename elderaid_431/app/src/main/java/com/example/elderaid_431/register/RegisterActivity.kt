package com.example.elderaid_431.register

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        database = Firebase.database.reference

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val name = etName.text.toString()
            val userType = spinnerUserType.selectedItem.toString()

            registerUser(email, password, name, userType)
        }
    }

    private fun registerUser(email: String, password: String, name: String, userType: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                val user = mapOf(
                    "name" to name,
                    "userType" to if (userType == "Yaşlı") 1 else 2
                )
                userId?.let {
                    database.child("users").child(it).setValue(user)
                    Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(this, "Kayıt başarısız!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
