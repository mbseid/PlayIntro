class dashboard
	init: () ->
		$("#postThread").click (event) ->
			jsRoutes.controllers.Transaction.post().ajax
				url: "/post"
				type: "POST"
				context: this
				data:
					accountNumber: "ABC123"
					amount: "2.02"
				success: (data) ->
					false
		false
	message: (msg) ->
	 	messages = msg.split("#")
	 	@handleMessage message for message in messages
	 	false

	handleMessage: (msg) ->
		split = msg.split(":")
		type = split[0]
		if split[1] == ""
			return
		switch type
			when "transactions" then @addTransactions(split[1])
			when "accounts" then @updateAccounts(split[1])
			when "transactionTotal" then @updateTransactionTotal(split[1])

		false

	addTransactions: (transactionsString) ->
		transactions = transactionsString.split(";")
		#remove the last element.  Needed because the split leaves the a rouge element
		transactions.pop()
		for t in transactions
			$("#transactions").append("<li>"+t+"</li>")

		false


	updateAccounts: (accountsString) ->
		#"account-"+a.id+",balance-"+a.balance+";"
		accounts = accountsString.split(';');
		accounts.pop()
		for a in accounts
			accountSplit = a.split(",")
			account = accountSplit[0]
			balance = accountSplit[1]
			$("#"+account).children(".balance").html(balance)
		
		false

	updateTransactionTotal: (transactionTotal) ->
		$("#totalTransactions").html(transactionTotal)



window.dashboard = new dashboard();