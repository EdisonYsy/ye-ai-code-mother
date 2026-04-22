# AI 零代码应用生成平台
基于大模型的“零代码应用生成”平台：用户用自然语言描述需求，系统自动生成网站 / 前端工程，支持实时预览、可视化编辑、源码下载与一键部署分享。

---

## 项目亮点

- **智能代码生成（SSE 流式）**：通过对话生成代码，流式输出让用户实时看到生成过程与工具调用结果。
- **可视化编辑**：生成后页面可在右侧 iframe 预览，进入编辑模式可选中元素并继续对话精确修改。
- **一键部署与分享**：将生成产物部署到本地部署目录并返回可访问 URL；支持下载完整项目源码（zip）。
- **对话历史 + 记忆**：对话历史落库（`chat_history`），对话记忆存储在 Redis（LangChain4j ChatMemoryStore）。
- **工作流扩展**：内置基于 LangGraph4j 的工作流代码（可用于更复杂的多阶段编排）。

---

## 核心能力说明

### 1）生成（对话驱动）
- 前端使用 `EventSource` 连接后端 SSE 接口，实时展示 AI 输出。
- 后端根据应用的 `codeGenType` 选择不同生成策略：
  - **HTML / 多文件**：流结束后解析并落盘到生成目录
  - **Vue 工程**：通过工具调用（写文件 / 读文件 / 修改文件等）直接写入项目结构

相关接口（默认 `server.servlet.context-path=/api`）：
- `GET /api/app/chat/gen/code?appId=...&message=...`（SSE）

### 2）预览（静态资源服务）
生成产物会被写入本地目录（默认在项目根目录下 `tmp/`），后端提供静态资源访问：
- `GET /api/static/{codeGenType}_{appId}/**`

### 3）可视化编辑（iframe + postMessage）
前端会向 iframe 注入编辑脚本：悬浮高亮、点击选中元素。选中信息会拼接到下一次用户提示词中，让 AI 按元素上下文修改内容。

### 4）部署、截图与封面
- 部署：复制生成产物到部署目录，返回可访问 URL
- 截图：异步打开部署 URL 截图并上传对象存储（COS），更新应用封面 `cover`

相关接口：
- `POST /api/app/deploy`
- `GET /api/app/download/{appId}`

---

## 技术栈

### 后端
- Spring Boot 3（Java 21）
- LangChain4j（OpenAI 兼容接口、工具调用、对话记忆）
- Redis（Spring Session、对话记忆、缓存扩展点）
- MySQL + MyBatis-Flex
- Caffeine（AI Service 实例缓存）
- Selenium + WebDriverManager（网页截图）
- COS SDK（封面上传）

### 前端
- Vue 3 + TypeScript + Vite
- Ant Design Vue
- Pinia + Vue Router
- SSE（EventSource）
- iframe + postMessage（父子页面通信、可视化编辑）

---

## 快速开始

### 1）准备环境
- MySQL：创建数据库并执行 `sql/create_table.sql`
- Redis：本地启动 Redis（用于 session / 记忆存储）
- Node.js：用于前端开发运行

### 2）后端启动
配置文件：
- `src/main/resources/application.yaml`

关键配置（默认）：
- 端口：`8126`
- context-path：`/api`
- MySQL：`spring.datasource.*`
- Redis：`spring.data.redis.*`

启动：

```bash
mvn spring-boot:run
```

### 3）前端启动

```bash
cd ye-ai-code-mother-frontend
npm install
npm run dev
```

开发环境下前端默认通过 `/api` 访问后端（依赖 Vite 代理或环境变量配置）。

---

## 目录结构（简要）

```
.
├── src/main/java/com/ye/yeaicodemother
│   ├── controller              # App / User / ChatHistory / Static / Workflow 等控制器
│   ├── service                 # 业务服务（应用、对话历史、截图等）
│   ├── ai                      # AI Service 工厂、工具、护轨等
│   ├── core                    # 生成/解析/保存/流式处理
│   ├── config                  # Redis / COS / 模型配置
│   └── langgraph4j             # LangGraph4j 工作流相关代码
├── src/main/resources
│   ├── application.yaml
│   └── mapper
├── sql
└── ye-ai-code-mother-frontend  # Vue3 前端
```

---

## 常见问题

### 1）前端空白页 / 接口 404
多数是前端 API baseURL 或 Vite 代理配置与后端端口不一致导致。检查：
- `ye-ai-code-mother-frontend/.env.development`
- `ye-ai-code-mother-frontend/vite.config.ts`
- 后端端口是否为 `8126` 且 context-path 为 `/api`

### 2）Spring 注入模型 Bean 冲突
若容器内存在多个 `ChatModel` / `StreamingChatModel` Bean，需要在注入处使用 `@Qualifier` / `@Resource(name=...)` 或指定 `@Primary`，避免启动时报 “required a single bean, but N were found”。

### 3）外部图片/插画工具测试不稳定
若单测依赖外部站点/接口，可能因网络或接口结构变更返回空数据导致断言失败。建议使用 mock 或契约测试替代直连外部资源。
