package com.tmdbapi.cowlar.task.ui.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import com.tmdbapi.cowlar.task.R
import com.tmdbapi.cowlar.task.databinding.ActivityTicketBinding
import com.tmdbapi.cowlar.task.utility.CommonFunctions.shortToast
import java.sql.DriverManager
import java.util.Calendar
import java.util.Date

class TicketActivity : AppCompatActivity() {

    var Success = true
    var bookingError = ""
    var alertDialogBuilder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null

    /*example of seats string to define how the auditorium looks like. We will adjust it properly later.*/
    var seats = ("_UUUUUAAAUU_/"
            + "__________/"
            + "UU_AAAUUU_UU/"
            + "UU_UUAAAA_AA/"
            + "AA_AAAAAA_AA/"
            + "AA_AAUUUU_AA/"
            + "UU_UUUUUU_AA/"
            + "__________/")
    var reservedSeats: ArrayList<Int> = ArrayList()
    var seatSize = 90
    var seatGaping: Int = 10
    var chosenSeats: Int = 0
    var count: Int = 0
    var STATUS_AVAILABLE = 1
    var STATUS_BOOKED: Int = 2
    var audiID: Int = 0
    var movieID: Int = 0
    var screening_id: Int = 0
    private var time = ""
    private var selectedIds: String? = ""
    private var costumerName: kotlin.String? = ""
    private var costumerEmail: kotlin.String? = ""
    private var costumerPhone: kotlin.String? = ""
    private var movieTitle: kotlin.String? = ""
    lateinit var binding: ActivityTicketBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            val intent = intent
            audiID = intent.getIntExtra("audiID", 0)
            movieID = intent.getIntExtra("movieID", 0)
            time = intent.getStringExtra("time")!!
            costumerName = intent.getStringExtra("name")
            costumerEmail = intent.getStringExtra("email")
            costumerPhone = intent.getStringExtra("number")
            movieTitle = intent.getStringExtra("title")
            chosenSeats = intent.getIntExtra("number of seats", 0)
        } catch (e: Exception) {
            shortToast(R.string.no_video_data_found)
           finish()
        }

        binding.tvTitle.text=movieTitle
        binding.tvMovieDate.text=time
        initClickEvent()
        initCalanderView()
    }
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0
    private fun initCalanderView() {
        // set current date to calendar and current month to currentMonth variable
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        // enable white status bar with black icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }

        // calendar view manager is responsible for our displaying logic
        val myCalendarViewManager = object :
            CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                // set date to calendar according to position where we are
                val cal = Calendar.getInstance()
                cal.time = date
                // if item is selected we return this layout items
                // in this example. monday, wednesday and friday will have special item views and other days
                // will be using basic item view
                return if (isSelected)
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> R.layout.first_special_selected_calendar_item
                        Calendar.WEDNESDAY -> R.layout.second_special_selected_calendar_item
                        Calendar.FRIDAY -> R.layout.third_special_selected_calendar_item
                        else -> R.layout.selected_calendar_item
                    }
                else
                // here we return items which are not selected
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> R.layout.first_special_calendar_item
                        Calendar.WEDNESDAY -> R.layout.second_special_calendar_item
                        Calendar.FRIDAY -> R.layout.third_special_calendar_item
                        else -> R.layout.calendar_item
                    }

                // NOTE: if we don't want to do it this way, we can simply change color of background
                // in bindDataToCalendarView method
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {

                // using this method we can bind data to calendar view
                // good practice is if all views in layout have same IDs in all item views
                holder.itemView.findViewById<TextView>(R.id.tv_date_calendar_item).text = DateUtils.getDayNumber(date)
                holder.itemView.findViewById<TextView>(R.id.tv_day_calendar_item).text = DateUtils.getDay3LettersName(date)

            }
        }

        // using calendar changes observer we can track changes in calendar
        val myCalendarChangesObserver = object :
            CalendarChangesObserver {
            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                var calanderDate="${DateUtils.getMonthName(date)}, ${DateUtils.getDayNumber(date)} "
                binding.tvMovieDate.text = calanderDate.toString()
//                tvDay.text = DateUtils.getDayName(date)
                super.whenSelectionChanged(isSelected, position, date)
            }


        }

        // selection manager is responsible for managing selection
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                // set date to calendar according to position
                val cal = Calendar.getInstance()
                cal.time = date
                // in this example sunday and saturday can't be selected, others can
                return when (cal[Calendar.DAY_OF_WEEK]) {
                    Calendar.SATURDAY -> false
                    Calendar.SUNDAY -> false
                    else -> true
                }
            }
        }

        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
        val singleRowCalendar = binding.mainSingleRowCalendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(getFutureDatesOfCurrentMonth())
            init()
        }

//        btnRight.setOnClickListener {
//            singleRowCalendar.setDates(getDatesOfNextMonth())
//        }
//
//        btnLeft.setOnClickListener {
//            singleRowCalendar.setDates(getDatesOfPreviousMonth())
//        }
    }



    private fun getDatesOfNextMonth(): List<Date> {
        currentMonth++ // + because we want next month
        if (currentMonth == 12) {
            // we will switch to january of next year, when we reach last month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] + 1)
            currentMonth = 0 // 0 == january
        }
        return getDates(mutableListOf())
    }

    private fun getDatesOfPreviousMonth(): List<Date> {
        currentMonth-- // - because we want previous month
        if (currentMonth == -1) {
            // we will switch to december of previous year, when we reach first month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] - 1)
            currentMonth = 11 // 11 == december
        }
        return getDates(mutableListOf())
    }

    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        currentMonth = calendar[Calendar.MONTH]
        return getDates(mutableListOf())
    }


    private fun getDates(list: MutableList<Date>): List<Date> {
        // load dates of whole month
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calendar.time)
        while (currentMonth == calendar[Calendar.MONTH]) {
            calendar.add(Calendar.DATE, +1)
            if (calendar[Calendar.MONTH] == currentMonth)
                list.add(calendar.time)
        }
        calendar.add(Calendar.DATE, -1)
        return list
    }

    private fun initClickEvent() {
        binding.btnGetTicket.setOnClickListener {
            if (chosenSeats < 0) {
                Toast.makeText(
                    this@TicketActivity,
                    "More seats have been selected!!",
                    Toast.LENGTH_LONG
                ).show()
            } else if (chosenSeats > 0) {
                Toast.makeText(
                    this@TicketActivity,
                    "Fewer seats have been selected!!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val seatsToBook = selectedIds!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                Task1(3, seatsToBook).execute()
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    throw java.lang.RuntimeException(e)
                }
                if (bookingError.isEmpty()) {
                    /*A thread for sending a message to prevent running the sendMessage() method from the main thread.
                             * Also for displaying a message after a successful booking.*/
                    val thread = Thread {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                                sendMessage()
                                try {
                                    Thread.sleep(5000)
                                } catch (e: InterruptedException) {
                                    throw java.lang.RuntimeException(e)
                                }
                                alertDialog!!.dismiss()
                                try {
                                    Thread.sleep(100)
                                } catch (e: InterruptedException) {
                                    throw java.lang.RuntimeException(e)
                                }

                                /*After booking the tickets and displaying the message
                                                  * We finish this activity and back to the main activity.*/
                                val intent = Intent(
                                    this@TicketActivity,
                                    HomeActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            } else {
                                requestPermissions(
                                    arrayOf(Manifest.permission.SEND_SMS),
                                    1
                                )
                            }
                        }
                    }
                    thread.start()
                } else {

                    /*After failing to book the tickets and displaying the error message
                             * We finish this activity and back to the main activity.*/
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        initSeatSelections()

    }

    private fun initSeatSelections() {
        seats = "/$seats"

        val layoutSeat = LinearLayout(this)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutSeat.orientation = LinearLayout.VERTICAL
        layoutSeat.layoutParams = params
        layoutSeat.setPadding(5 * seatGaping, 5 * seatGaping, 5 * seatGaping, 5 * seatGaping)
        binding.layoutSeat.addView(layoutSeat)

        var layout: LinearLayout? = null

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

        buildSeats(reservedSeats)

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

        /*For loop to build the seats groupView from the seats string which contains how the view of the seats
        * looks like, and set the margins, the id's, colors, etc..*/

        /*For loop to build the seats groupView from the seats string which contains how the view of the seats
        * looks like, and set the margins, the id's, colors, etc..*/for (index in 0 until seats.length) {
            if (seats[index] == '/') {
                layout = LinearLayout(this)
                layout.orientation = LinearLayout.HORIZONTAL
                layoutSeat.addView(layout)
            } else if (seats[index] == 'U') {
                count++
                val view = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(seatSize, seatSize)
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping)
                view.layoutParams = layoutParams
                view.setPadding(0, 0, 0, 2 * seatGaping)
                view.id = count
                view.gravity = Gravity.CENTER
                view.setBackgroundResource(R.drawable.ic_seats_booked)
                view.setTextColor(Color.WHITE)
                view.tag = STATUS_BOOKED
                view.text = count.toString() + ""
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f)
                layout!!.addView(view)
                view.setOnClickListener {
                    if (view.tag as Int == STATUS_AVAILABLE) {
                        if (selectedIds!!.contains(view.id.toString() + ",")) {
                            selectedIds = selectedIds!!.replace(view.id.toString() + ",", "")
                            view.setBackgroundResource(R.drawable.ic_seats_book)
                            chosenSeats++
                        } else {
                            selectedIds = selectedIds + view.id + ","
                            view.setBackgroundResource(R.drawable.ic_seats_selected)
                            chosenSeats--
                        }
                    } else if (view.tag as Int == STATUS_BOOKED) {
                        Toast.makeText(this, "Seat " + view.id + " is Booked", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else if (seats[index] == 'A') {
                count++
                val view = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(seatSize, seatSize)
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping)
                view.layoutParams = layoutParams
                view.setPadding(0, 0, 0, 2 * seatGaping)
                view.id = count
                view.gravity = Gravity.CENTER
                view.setBackgroundResource(R.drawable.ic_seats_book)
                view.text = count.toString() + ""
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f)
                view.setTextColor(Color.BLACK)
                view.tag = STATUS_AVAILABLE
                layout!!.addView(view)
//                seatViewList.add(view)
                view.setOnClickListener {
                    if (view.tag as Int == STATUS_AVAILABLE) {
                        if (selectedIds!!.contains(view.id.toString() + ",")) {
                            selectedIds = selectedIds!!.replace(view.id.toString() + ",", "")
                            view.setBackgroundResource(R.drawable.ic_seats_book)
                            chosenSeats++
                        } else {
                            selectedIds = selectedIds + view.id + ","
                            view.setBackgroundResource(R.drawable.ic_seats_selected)
                            chosenSeats--
                        }
                    } else if (view.tag as Int == STATUS_BOOKED) {
                        Toast.makeText(this, "Seat " + view.id + " is Booked", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else if (seats[index] == '_') {
                val view = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(seatSize, seatSize)
                layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping)
                view.layoutParams = layoutParams
                view.setBackgroundColor(Color.TRANSPARENT)
                view.text = ""
                layout!!.addView(view)
            }
        }
    }

    /*This method iterates on seats String, for each seat it checks if the seat is on reservedSeat list or not,
        * if yes we define the seat as a booked seat, otherwise we define the seat as a available seat.*/
    fun buildSeats(tmp: List<Int?>) {
        val fac = if (audiID == 1) 0 else (audiID - 1) * 60
        var cnt = 0
        for (i in 0 until seats.length) {
            if (seats.get(i) == 'U' || seats.get(i) == 'A') {
                cnt++
                if (tmp.contains(cnt + fac)) {
                    seats = seats.substring(0, i) + 'U' + seats.substring(i + 1)
                } else {
                    seats = seats.substring(0, i) + 'A' + seats.substring(i + 1)
                }
            }
        }
    }

    /*This procedure sends a message to the costumer after a successful booking.
        * And displays a message after successful booking and sending message.*/
    fun sendMessage() {
        var error = ""
        val message = """
             Dear $costumerName,
             We have booked your seats!. Please arrive before the screening time in order to pay and issue the tickets.
             Best regards, EA6Cinema.
             """.trimIndent()
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(costumerPhone, null, message, null, null)
        } catch (e: Exception) {
            Toast.makeText(
                this@TicketActivity, "There is an error" +
                        " messaging you, Please try again later!", Toast.LENGTH_LONG
            ).show()
            error = e.toString()
            e.printStackTrace()
        }
        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            throw java.lang.RuntimeException(e)
        }
        if (error.isEmpty()) {
            Success = true
            this@TicketActivity.runOnUiThread(Runnable {
                alertDialogBuilder = AlertDialog.Builder(this@TicketActivity)
                alertDialogBuilder?.setMessage(
                    """
                    Hi $costumerName.
                    Thank you for booking a movie tickets. We look forward to seeing you and your group!:)
                    """.trimIndent()
                )
                alertDialogBuilder?.setTitle("Order status")
                alertDialogBuilder?.setIcon(R.drawable.baseline_favorite_24)
                alertDialog = alertDialogBuilder?.create()
                alertDialog?.show()
            })
        }
    }

    /*This inner class have three modes:
        * mode == 1, To retrieve the appropriate screening id.
        * mode == 2, To retrieve the reserved seats of the specific screening id.
        * mode == 3, To update the reservation and seats_reserved tables after a successful booking.*/
    inner class Task1 : AsyncTask<Void?, Void?, Void?> {
        var error = ""
        lateinit var seatsToBook: Array<String>
        private var mode = 0

        constructor(mode: Int) {
            this.mode = mode
        }

        constructor(mode: Int, seatsToBook: Array<String>) {
            this.mode = mode
            this.seatsToBook = seatsToBook
        }


        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            Toast.makeText(
                    this@TicketActivity,
                    "There is error with the booking, please try again later!", Toast.LENGTH_LONG
                ).show()
            }


        override fun doInBackground(vararg p0: Void?): Void? {
            if (mode == 1) {
                try {
                    Class.forName("com.mysql.jdbc.Driver")
                    val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/ea6cinema_db",
                        "andro",
                        "andro"
                    )
                    val statement1 = conn.createStatement()
                    val resultSet = statement1.executeQuery(
                        "SELECT * FROM `ea6cinema_db`.`screening` " +
                                "where movie_id = '" + movieID + "' AND audit_id =" +
                                " '" + audiID + "' AND screening_time = '" + time + "'"
                    )
                    if (resultSet.next()) {
                        screening_id = resultSet.getInt("id")
                    }
                    statement1.close()
                    conn.close()
                } catch (e: Exception) {
                    error = e.toString()
                }
            } else if (mode == 2) {
                try {
                    Class.forName("com.mysql.jdbc.Driver")
                    val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/ea6cinema_db",
                        "andro",
                        "andro"
                    )
                    val statement1 = conn.createStatement()
                    val resultSet = statement1.executeQuery(
                        "SELECT * FROM `ea6cinema_db`.`seat_reserved` " +
                                "where screening_id = '" + screening_id + "' "
                    )
                    while (resultSet.next()) {
                        reservedSeats.add(resultSet.getInt("seat_id"))
                    }
                    statement1.close()
                    conn.close()
                } catch (e: Exception) {
                    error = e.toString()
                }
            } else if (mode == 3) {
                try {
                    Class.forName("com.mysql.jdbc.Driver")
                    val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/ea6cinema_db",
                        "andro",
                        "andro"
                    )
                    val statement1 = conn.createStatement()
                    val statement2 = conn.createStatement()
                    var resultSet =
                        statement1.executeQuery("SELECT * FROM `ea6cinema_db`.`reservation`")
                    var resID = 1
                    while (resultSet.next()) {
                        resID++
                    }
                    statement2.executeUpdate(
                        "INSERT INTO `ea6cinema_db`.`reservation` " +
                                "(`id`, `screening_id`, `name`, `email`, `phone`) VALUES " +
                                "('" + resID + "','" + screening_id + "','" + costumerName + "','"
                                + costumerEmail + "', '" + costumerPhone + "')"
                    )
                    resultSet =
                        statement1.executeQuery("SELECT * FROM `ea6cinema_db`.`seat_reserved`")
                    var id = 1
                    while (resultSet.next()) {
                        id++
                    }
                    for (i in seatsToBook.indices) {
                        var seat = seatsToBook[i].toInt()
                        seat = seat + (audiID - 1) * 60
                        statement2.executeUpdate(
                            "INSERT INTO `ea6cinema_db`.`seat_reserved` " +
                                    "(`id`, `seat_id`, `reservation_id`, `screening_id`) VALUES " +
                                    "('" + id + "','" + seat + "','" + resID + "','" + screening_id + "')"
                        )
                        id++
                    }
                    statement1.close()
                    conn.close()
                } catch (e: Exception) {
                    error = e.toString()
                    bookingError = error
                }
            }
            return null
        }
    }

}
