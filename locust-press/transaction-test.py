from locust import HttpUser, task, between
import random
import string
import logging
from decimal import Decimal

# 生成随机整数作为 long 类型的数据
def random_long():
    return random.randint(1000000000, 9999999999)

# 生成随机字符串
def random_string(length):
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for i in range(length))

class TransactionUser(HttpUser):
    # 每个任务执行之间的等待时间范围，单位为秒
    wait_time = between(1, 5)

    @task(2)
    def list_transactions(self):
        """
        测试列出所有交易的接口
        """
        # 发送 GET 请求到列出所有交易的接口
        self.client.get("/api/transactions?page=1&size=10")

    @task(3)
    def create_transaction(self):
        """
        测试创建交易的接口
        """
        # 生成随机的交易数据
        source_account_id = random_long()
        target_account_id = random_long()
        transaction_no = random_string(5)
        amount = Decimal(str(round(random.uniform(1, 1000), 2)))
        description = random_string(10)

        transaction_data = {
            "sourceAccountId": source_account_id,
            "targetAccountId": target_account_id,
            "transactionNo": transaction_no,
            "amount": str(amount),
            "description": description
        }
        # 发送 POST 请求到创建交易的接口
        response = self.client.post("/api/transactions", json=transaction_data)
        if response.status_code == 201:
            # 如果创建成功，记录交易 ID 用于后续的修改和删除操作
            transaction_id = response.json().get("id")
            if transaction_id:
                self.created_transaction_ids.append(transaction_id)
                logging.info(f"Created transaction with ID: {transaction_id}")
            else:
                logging.warning("Failed to get transaction ID from response.")
        else:
            logging.warning(f"Create transaction failed with status code: {response.status_code}")

    @task(1)
    def update_transaction(self):
        """
        测试修改交易的接口
        """
        if len(self.created_transaction_ids) < 10:  # 假设阈值为 10
            self.create_transaction()
        if self.created_transaction_ids:
            # 随机选择一个已创建的交易 ID
            transaction_id = random.choice(self.created_transaction_ids)
            # 生成新的交易数据
            source_account_id = random_long()
            target_account_id = random_long()
            transaction_no = random_string(5)
            amount = Decimal(str(round(random.uniform(1, 1000), 2)))
            description = random_string(10)

            updated_transaction_data = {
                "id": transaction_id,
                "sourceAccountId": source_account_id,
                "targetAccountId": target_account_id,
                "transactionNo": transaction_no,
                "amount": str(amount),
                "description": description
            }
            # 发送 PUT 请求到修改交易的接口
            self.client.put(f"/api/transactions/{transaction_id}", json=updated_transaction_data)

    @task(1)
    def delete_transaction(self):
        """
        测试删除交易的接口
        """
        if len(self.created_transaction_ids) < 10:  # 假设阈值为 10
            self.create_transaction()
        if self.created_transaction_ids:
            # 随机选择一个已创建的交易 ID
            transaction_id = random.choice(self.created_transaction_ids)
            # 发送 DELETE 请求到删除交易的接口
            self.client.delete(f"/api/transactions/{transaction_id}")
            # 从已创建的交易 ID 列表中移除该 ID
            self.created_transaction_ids.remove(transaction_id)

    @task(1)
    def get_transaction_by_id(self):
        """
        测试根据交易 ID 查询交易信息的接口
        """
        if len(self.created_transaction_ids) < 10:  # 假设阈值为 10
            self.create_transaction()
        if self.created_transaction_ids:
            # 随机选择一个已创建的交易 ID
            transaction_id = random.choice(self.created_transaction_ids)
            # 发送 GET 请求到根据 ID 查询交易的接口
            self.client.get(f"/api/transactions/{transaction_id}")

    def on_start(self):
        """
        在用户开始执行任务前初始化已创建的交易 ID 列表
        """
        self.created_transaction_ids = []